package io.mtini.model;

import android.content.Context;

import android.util.ArrayMap;
import android.util.Log;

import com.google.gson.Gson;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

import org.bitcoinj.core.ECKey;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.Nullable;

import io.mtini.android.http.GenericRemoteRESTTask;
import io.mtini.android.http.RemoteRESTTask;

import com.prelimtek.android.basecomponents.dao.BaseDAOInterface;
import com.prelimtek.utils.blockchain.SawtoothUtils;
import com.prelimtek.utils.crypto.Wallet;
import com.prelimtek.android.picha.dao.MediaDAOInterface;

import io.mtini.proto.EATRequestResponseProtos;
import io.mtini.proto.EstateAccountProtos;
import io.mtini.proto.MtiniWalletProtos;
import sawtooth.sdk.protobuf.BatchList;

//TODO exception handling for logging and reporting - make it bubble up to UI. Call a service?
public class RemoteDAO extends AbstractDAO implements MediaDAOInterface,BaseDAOInterface {

    private static String TAG = Class.class.getSimpleName();

    private RemoteDAO() {
        super();
    }

    private RemoteDAO(Context _context) {
        super(_context);
    }

    public static RemoteDAO builder(Context context) {

        return new RemoteDAO(context);
    }


    @Override
    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public BaseDAOInterface open() {
        return this;
    }

    @Override
    public BaseDAOInterface open(Context context) {
        return this;
    }

    @Override
    public void close(){
        context = null;
    }


    private String retrieveCustomerId() throws RemoteDAOException{

        //SharedPreferences pref = context.getApplicationContext().getSharedPreferences(Configuration.SERVER_SIDE_PREFERENCES_TAG, Context.MODE_PRIVATE);
        //String customerId = pref.getString("id", null);
        String customerId = config.customerId;

        if(customerId==null)throw new RemoteDAOException("Customer id cannot be null");
        return customerId;
    }




    public String retrieveServerSigner(SecurityModel sModel) throws RemoteDAOException{

        Gson gson = new Gson();
        String objJson = gson.toJson(sModel);
        EATRequestResponseProtos.EATRequestResponse.Request request =  EATRequestResponseProtos.EATRequestResponse.Request.newBuilder().setJsonRequest(objJson).build();
        String sharedKey = null;
        try {
        GenericRemoteRESTTask task = GenericRemoteRESTTask.Builder.newBuilder(GenericRemoteRESTTask.TYPE.POST)
        //RemoteRESTTask task = RemoteRESTTask.Builder.newBuilder(RemoteRESTTask.TYPE.GET)
                .setHost(host)
                .setPath("/api/v1/security")
                .setComponent("authenticate")
                .addQuery("authService", sModel.getAuthService() )
                .addQuery("userName", encode(sModel.getUserName()))
                .addQuery("phoneNumber", encode(sModel.getPhoneNumber()) )
                .addQuery("email", encode(sModel.getEmail()) )
                .addQuery("token", sModel.getToken() )
                .setRemoteDAOListener(
                        new RemoteDAOListener() {

                                @Override
                                public void onRequestComplete(EATRequestResponseProtos.EATRequestResponse.Response response) {
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e(TAG,e.getMessage(),e);
                                    //DialogUtils.startErrorDialog( context,e.getMessage());
                                }

                        })
                .build();
        task.execute(request);

        EATRequestResponseProtos.EATRequestResponse.Response response = null;

            response = task.get(20,TimeUnit.SECONDS);
            if(response==null)throw new RemoteDAOException("A server side error occurred. Remote service not reachable.");
            JSONObject resJson = new JSONObject(response.getJsonResponse());
            if(resJson.has("error") || !resJson.has("result")){
                Log.e(TAG,resJson.getString("error"));
                throw new RemoteDAOException("A server side error occurred. "+resJson.getString("error"));
            }

            sharedKey = resJson.getString("result");

        } catch (InterruptedException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network interruption error.");
        } catch (ExecutionException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote execution error.");
        } catch (TimeoutException e) {
            Log.e(TAG,e.toString());
            throw new RemoteDAOException("Network timeout error.");
        }  catch (RemoteDAOException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network security error: "+e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Data format error : "+e.getMessage());
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Data format error  Contact admin. : "+e.getMessage());
        }

        return sharedKey;
    }

    public boolean deleteEstate(EstateModel property) throws RemoteDAOException {

        BatchList batches = null;
        EstateAccountProtos.LedgerEntries entries = null;
        try {


                Wallet wallet = getMyAuthedWallet();
                ECKey privateKey = ECKey.fromPrivate(wallet.getPrivateKeyBytes());
                byte[] publicKey = wallet.getPublicKeyUTF8Bytes();
                String dataAddress = SawtoothUtils.calculateAddress(SawtoothUtils.FAMILY,publicKey);

            entries = toEntries(property).setOperation(EstateAccountProtos.Operation.DELETE_ESTATE).build();

            batches = SawtoothUtils.createBatchList(privateKey, entries.toByteString());

            EATRequestResponseProtos.EATRequestResponse.Request request =  EATRequestResponseProtos.EATRequestResponse.Request
                    .newBuilder()
                    .setData(batches.toByteString())
                    .setEntries(entries)
                    .build();

            GenericRemoteRESTTask task = GenericRemoteRESTTask.Builder.newBuilder(GenericRemoteRESTTask.TYPE.POST)
                    .setHeader("x-mtini-apikey", this.retrieveAPIKeyFromPrefs())
                    .setHost(host)
                    .setPath("/api/v1/eatproxy")
                    .setCustomerId(retrieveCustomerId())
                    .setComponent("estate")
                    .setComponentAction("delete_estate")
                    .addQuery("dataAddress",dataAddress)

                    //TODO implement http status listener incase of bad connectivity etc
                    .setRemoteDAOListener(new
                                                  RemoteDAOListener() {
                                                      @Override
                                                      public void onRequestComplete(EATRequestResponseProtos.EATRequestResponse.Response response) {

                                                      }

                                                      @Override
                                                      public void onError(Throwable e) {
                                                          Log.e(TAG,e.getMessage(),e);
                                                          //DialogUtils.startErrorDialog( context,e.getMessage());
                                                      }
                                                  })
                    .build();
            task.execute(request);


            EATRequestResponseProtos.EATRequestResponse.Response response = task.get(10,TimeUnit.SECONDS);
            if( response==null) throw new RemoteDAOException("Null response");
            JSONArray jsonArray = new JSONObject(response.getJsonResponse()).getJSONArray("elastic");
            if(jsonArray.length()>0) {
                JSONObject json = jsonArray.getJSONObject(0);
                if (json.getString("result").equalsIgnoreCase("updated"))
                    return true;
            }
        } catch (InterruptedException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network interruption error.");
        } catch (ExecutionException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote execution error.");
        } catch (TimeoutException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network timeout error.");
        } catch (JSONException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote data error.");
        } catch (InvalidProtocolBufferException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote data error.");
        } catch (CloneNotSupportedException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote data error.");
        } catch (RemoteDAOException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network security error.");
        }catch (AbstractDAOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RemoteDAOException("APIkey error");
        }catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
                Log.e(TAG, e.getMessage(), e);
                throw new RemoteDAOException("Local security error.");
        }

        return false;

    }

    public EstateModel updateEstate(EstateModel newProperty) throws RemoteDAOException {

        EstateAccountProtos.LedgerEntries entries = null;
        try {

            Wallet wallet = getMyAuthedWallet();
            ECKey privateKey = ECKey.fromPrivate(wallet.getPrivateKeyBytes());
            byte[] publicKey = wallet.getPublicKeyUTF8Bytes();
            String dataAddress = SawtoothUtils.calculateAddress(SawtoothUtils.FAMILY,publicKey);

            entries = toEntries(newProperty).setOperation(EstateAccountProtos.Operation.EDIT_ESTATE).build();

            BatchList batches = SawtoothUtils.createBatchList(privateKey, entries.toByteString());

            EATRequestResponseProtos.EATRequestResponse.Request request =  EATRequestResponseProtos.EATRequestResponse.Request
                    .newBuilder()
                    .setData(batches.toByteString())
                    .setEntries(entries)
                    .build();

            GenericRemoteRESTTask task = GenericRemoteRESTTask.Builder.newBuilder(GenericRemoteRESTTask.TYPE.POST)
                .setHeader("x-mtini-apikey", this.retrieveAPIKeyFromPrefs())
                .setHost(host)
                .setPath("/api/v1/eatproxy")
                .setCustomerId(retrieveCustomerId())
                .setComponent("estate")
                .setComponentAction("edit_estate")
                .addQuery("dataAddress",dataAddress)
                //TODO implement http status listener incase of bad connectivity etc
                .setRemoteDAOListener(new
                                              RemoteDAOListener() {
                                                  @Override
                                                  public void onRequestComplete(EATRequestResponseProtos.EATRequestResponse.Response response) {

                                                  }

                                                  @Override
                                                  public void onError(Throwable e) {
                                                      Log.e(TAG,e.getMessage(),e);
                                                      //DialogUtils.startErrorDialog( context,e.getMessage());
                                                  }
                                              })
                .build();
        task.execute(request);


            EATRequestResponseProtos.EATRequestResponse.Response response = task.get(10,TimeUnit.SECONDS);
            if( response==null) throw new RemoteDAOException("Null response");
            JSONArray jsonArray = new JSONObject(response.getJsonResponse()).getJSONArray("elastic");
            if(jsonArray.length()>0) {
                JSONObject json = jsonArray.getJSONObject(0);
                if (json.getString("result").equalsIgnoreCase("updated"))
                    return newProperty;
            }
        } catch (InterruptedException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network interruption error.");
        } catch (ExecutionException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote execution error.");
        } catch (TimeoutException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network timeout error.");
        } catch (JSONException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote data error.");
        } catch (InvalidProtocolBufferException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote data error.");
        } catch (CloneNotSupportedException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote data error.");
        } catch (RemoteDAOException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network security error.");
        }catch (AbstractDAOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RemoteDAOException("APIkey error");
        }catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RemoteDAOException("Local security error.");
        }

        return null;
    }

    public EstateModel addEstate(EstateModel newProperty) throws RemoteDAOException {

        EstateAccountProtos.LedgerEntries entries = null;

        try {

            Wallet wallet = getMyAuthedWallet();
            ECKey privateKey = ECKey.fromPrivate(wallet.getPrivateKeyBytes());
            byte[] publicKey = wallet.getPublicKeyUTF8Bytes();
            String dataAddress = SawtoothUtils.calculateAddress(SawtoothUtils.FAMILY,publicKey);

            entries = toEntries(newProperty).setOperation(EstateAccountProtos.Operation.ADD_ESTATE).build();

            BatchList batches = SawtoothUtils.createBatchList(privateKey, entries.toByteString());


            EATRequestResponseProtos.EATRequestResponse.Request request =  EATRequestResponseProtos.EATRequestResponse.Request
                    .newBuilder()
                    .setData(batches.toByteString())
                    .setEntries(entries)
                    .build();

            GenericRemoteRESTTask task = GenericRemoteRESTTask.Builder.newBuilder(GenericRemoteRESTTask.TYPE.POST)
                .setHeader("x-mtini-apikey", this.retrieveAPIKeyFromPrefs())
                .setHost(host)
                .setPath("/api/v1/eatproxy")
                .setCustomerId(retrieveCustomerId())
                .setComponent("estate")
                .setComponentAction("add_estate")
                .addQuery("dataAddress",dataAddress)
                //TODO implement http status listener incase of bad connectivity etc
                .build();

        task.execute(request);
        //EstateModel ret = null;

            EATRequestResponseProtos.EATRequestResponse.Response response = task.get(10,TimeUnit.SECONDS);
            if( response==null) throw new RemoteDAOException("Null response");
            JSONArray jsonArray = new JSONObject(response.getJsonResponse()).getJSONArray("elastic");
            if(jsonArray.length()>0) {
                JSONObject json = jsonArray.getJSONObject(0);
                String jsonResult = json.getString("result");
                if (jsonResult.equalsIgnoreCase("updated") || jsonResult.equalsIgnoreCase("created"))
                    return newProperty;
            }
        } catch (InterruptedException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network interruption error.");
        } catch (ExecutionException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote execution error.");
        } catch (TimeoutException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network timeout error.");
        } catch (JSONException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote data error.");
        } catch (InvalidProtocolBufferException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote data error.");
        } catch (CloneNotSupportedException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote data error.");
        } catch (RemoteDAOException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network security error.");
        }catch (AbstractDAOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RemoteDAOException("APIkey error");
        }catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RemoteDAOException("Local security error.");
        }

        return null;
    }

    public List<EstateModel> getMyEstateList() throws RemoteDAOException {
        List<EstateModel> ret = new ArrayList<EstateModel>();

        try {


            Wallet wallet = getMyAuthedWallet();
            byte[] publicKey = wallet.getPublicKeyUTF8Bytes();
            String dataAddress = SawtoothUtils.calculateAddress(SawtoothUtils.FAMILY,publicKey);


            RemoteRESTTask task = RemoteRESTTask.Builder.newBuilder(RemoteRESTTask.TYPE.GET)
                .setHeader("x-mtini-apikey", this.retrieveAPIKeyFromPrefs())
                .setHost(host)
                .setPath("/api/v1/eatproxy")
                .setCustomerId(retrieveCustomerId())
                .setComponent("estate")
                    .addQuery("dataAddress",dataAddress)
                //TODO implement http status listener incase of bad connectivity etc
                .build();
        task.execute();


            EATRequestResponseProtos.EATRequestResponse.Response response = task.get(10,TimeUnit.SECONDS);
            if( response==null) throw new RemoteDAOException("Null response");
            for(EstateAccountProtos.LedgerEntries.EstateModel estate:response.getEntries().getEstateDataList()){
                ret.add(toLocalModel(estate));
            }
        } catch (InterruptedException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network interruption error.");
        } catch (ExecutionException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote execution error.");
        } catch (TimeoutException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network timeout error.");
        }  catch (InvalidProtocolBufferException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote data error.");
        }  catch (RemoteDAOException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network security error.");
        }catch (AbstractDAOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RemoteDAOException("APIkey error");
        }catch (UnsupportedEncodingException  e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RemoteDAOException("Local security error.");
        }
        return ret;
    }

    public boolean deleteTenant(TenantModel tenant, EstateModel property) throws RemoteDAOException {
        EstateAccountProtos.LedgerEntries entries = null;
        try {


            Wallet wallet = getMyAuthedWallet();
            ECKey privateKey = ECKey.fromPrivate(wallet.getPrivateKeyBytes());
            byte[] publicKey = wallet.getPublicKeyUTF8Bytes();
            String dataAddress = SawtoothUtils.calculateAddress(SawtoothUtils.FAMILY,publicKey);

            entries = toEntries(tenant).setOperation(EstateAccountProtos.Operation.DELETE_TENANT).build();

            BatchList batches = SawtoothUtils.createBatchList(privateKey, entries.toByteString());


            EATRequestResponseProtos.EATRequestResponse.Request request =  EATRequestResponseProtos.EATRequestResponse.Request
                    .newBuilder()
                    .setData(batches.toByteString())
                    .setEntries(entries)
                    .build();

            GenericRemoteRESTTask task = GenericRemoteRESTTask.Builder.newBuilder(GenericRemoteRESTTask.TYPE.POST)
                    .setHeader("x-mtini-apikey", this.retrieveAPIKeyFromPrefs())
                    .setHost(host)
                    .setPath("/api/v1/eatproxy")
                    .setCustomerId(retrieveCustomerId())
                    .setComponent("tenant")
                    .setComponentAction("delete_tenant")
                    .addQuery("dataAddress",dataAddress)
                    .setRemoteDAOListener(new
                                                  RemoteDAOListener() {
                                                      @Override
                                                      public void onRequestComplete(EATRequestResponseProtos.EATRequestResponse.Response response) {

                                                      }

                                                      @Override
                                                      public void onError(Throwable e) {
                                                          Log.e(TAG,e.getMessage(),e);
                                                          //DialogUtils.startErrorDialog( context,e.getMessage());
                                                      }
                    })
                    .build();

            task.execute(request);



            EATRequestResponseProtos.EATRequestResponse.Response response = task.get(10,TimeUnit.SECONDS);
            if( response==null) throw new RemoteDAOException("Null response");
            JSONArray jsonArray = new JSONObject(response.getJsonResponse()).getJSONArray("elastic");
            if(jsonArray.length()>0) {
                JSONObject json = jsonArray.getJSONObject(0);

                String jsonResult = json.getString("result");
                if (jsonResult.equalsIgnoreCase("updated") || jsonResult.equalsIgnoreCase("created"))
                    return true;
            }
        } catch (InterruptedException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network interruption error.");
        } catch (ExecutionException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote execution error.");
        } catch (TimeoutException e) {
            Log.e(TAG,e.toString());
            throw new RemoteDAOException("Network timeout error.");
        } catch (JSONException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote data error.");
        } catch (InvalidProtocolBufferException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote data error.");
        } catch (CloneNotSupportedException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote data error.");
        } catch (RemoteDAOException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network security error.");
        }catch (AbstractDAOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RemoteDAOException("APIkey error");
        }catch (UnsupportedEncodingException | NoSuchAlgorithmException  e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RemoteDAOException("Local security error.");
        }

        return false;
    }


    public TenantModel addTenant(TenantModel newTenant, EstateModel estate) throws RemoteDAOException {
        EstateAccountProtos.LedgerEntries entries = null;

        try {

            Wallet wallet = getMyAuthedWallet();
            ECKey privateKey = ECKey.fromPrivate(wallet.getPrivateKeyBytes());
            byte[] publicKey = wallet.getPublicKeyUTF8Bytes();
            String dataAddress = SawtoothUtils.calculateAddress(SawtoothUtils.FAMILY,publicKey);

            entries = toEntries(newTenant).setOperation(EstateAccountProtos.Operation.ADD_TENANT).build();

            BatchList batches = SawtoothUtils.createBatchList(privateKey, entries.toByteString());


            EATRequestResponseProtos.EATRequestResponse.Request request =  EATRequestResponseProtos.EATRequestResponse.Request
                    .newBuilder()
                    .setData(batches.toByteString())
                    .setEntries(entries)
                    .build();

            GenericRemoteRESTTask task = GenericRemoteRESTTask.Builder.newBuilder(GenericRemoteRESTTask.TYPE.POST)
                .setHeader("x-mtini-apikey", this.retrieveAPIKeyFromPrefs())
                .setHost(host)
                .setPath("/api/v1/eatproxy")
                .setCustomerId(retrieveCustomerId())
                .setComponent("tenant")
                .setComponentAction("add_tenant")
                .addQuery("dataAddress",dataAddress)
                .setRemoteDAOListener(new
                                              RemoteDAOListener() {
                                                  @Override
                                                  public void onRequestComplete(EATRequestResponseProtos.EATRequestResponse.Response response) {

                                                  }

                                                  @Override
                                                  public void onError(Throwable e) {
                                                      Log.e(TAG,e.getMessage(),e);
                                                      //DialogUtils.startErrorDialog( context,e.getMessage());
                                                  }
                })
                .build();

        task.execute(request);


            EATRequestResponseProtos.EATRequestResponse.Response response = task.get(10,TimeUnit.SECONDS);
            if( response==null) throw new RemoteDAOException("Null response");
            JSONArray jsonArray = new JSONObject(response.getJsonResponse()).getJSONArray("elastic");
            if(jsonArray.length()>0) {
                JSONObject json = jsonArray.getJSONObject(0);
                String jsonResult = json.getString("result");
                if (jsonResult.equalsIgnoreCase("updated") || jsonResult.equalsIgnoreCase("created"))
                    return newTenant;
            }
        } catch (InterruptedException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network interruption error.");
        } catch (ExecutionException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote execution error.");
        } catch (TimeoutException e) {
            Log.e(TAG,e.toString());
            throw new RemoteDAOException("Network timeout error.");
        } catch (JSONException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote data error.");
        } catch (InvalidProtocolBufferException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote data error.");
        } catch (CloneNotSupportedException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote data error.");
        } catch (RemoteDAOException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network security error.");
        }catch (AbstractDAOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RemoteDAOException("APIkey error");
        }catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RemoteDAOException("Local security error.");
        }

        return null;
    }

    public TenantModel updateTenant(TenantModel newtenant, Object o) throws RemoteDAOException{
        EstateAccountProtos.LedgerEntries entries = null;
        try {

            Wallet wallet = getMyAuthedWallet();
            ECKey privateKey = ECKey.fromPrivate(wallet.getPrivateKeyBytes());
            byte[] publicKey = wallet.getPublicKeyUTF8Bytes();
            String dataAddress = SawtoothUtils.calculateAddress(SawtoothUtils.FAMILY,publicKey);

            entries = toEntries(newtenant).setOperation(EstateAccountProtos.Operation.EDIT_TENANT).build();

            BatchList batches = SawtoothUtils.createBatchList(privateKey, entries.toByteString());

            EATRequestResponseProtos.EATRequestResponse.Request request =  EATRequestResponseProtos.EATRequestResponse.Request
                    .newBuilder()
                    .setData(batches.toByteString())
                    .setEntries(entries)
                    .build();

            GenericRemoteRESTTask task = GenericRemoteRESTTask.Builder.newBuilder(GenericRemoteRESTTask.TYPE.POST)
                .setHeader("x-mtini-apikey", this.retrieveAPIKeyFromPrefs())
                .setHost(host)
                .setPath("/api/v1/eatproxy")
                .setCustomerId(retrieveCustomerId())
                .setComponent("tenant")
                .setComponentAction("edit_tenant")
                    .addQuery("dataAddress",dataAddress)
                    .setRemoteDAOListener(new
                                                  RemoteDAOListener() {
                                                      @Override
                                                      public void onRequestComplete(EATRequestResponseProtos.EATRequestResponse.Response response) {

                                                      }

                                                      @Override
                                                      public void onError(Throwable e) {
                                                          Log.e(TAG,e.getMessage(),e);
                                                          //DialogUtils.startErrorDialog( context,e.getMessage());
                                                      }
                                                  })
                .build();

            task.execute(request);



            EATRequestResponseProtos.EATRequestResponse.Response response = task.get(10,TimeUnit.SECONDS);
            if( response==null) throw new RemoteDAOException("Null response");
            JSONArray jsonArray = new JSONObject(response.getJsonResponse()).getJSONArray("elastic");
            if(jsonArray.length()>0) {
                JSONObject json = jsonArray.getJSONObject(0);

                String jsonResult = json.getString("result");
                if (jsonResult.equalsIgnoreCase("updated") || jsonResult.equalsIgnoreCase("created"))
                    return newtenant;
            }
        } catch (InterruptedException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network interruption error.");
        } catch (ExecutionException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote execution error.");
        } catch (TimeoutException e) {
            Log.e(TAG,e.toString());
            throw new RemoteDAOException("Network timeout error.");
        } catch (JSONException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote data error.");
        } catch (InvalidProtocolBufferException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote data error.");
        } catch (CloneNotSupportedException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote data error.");
        } catch (RemoteDAOException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network security error.");
        }catch (AbstractDAOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RemoteDAOException("APIkey error");
        }catch (UnsupportedEncodingException | NoSuchAlgorithmException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RemoteDAOException("Local security error.");
        }

        return null;
    }

    public List<TenantModel> getTenantList(EstateModel propertyInfo) throws RemoteDAOException {

        List<TenantModel> ret = new ArrayList<TenantModel>();

        try {

            Wallet wallet = getMyAuthedWallet();
            byte[] publicKey = wallet.getPublicKeyUTF8Bytes();
            String dataAddress = SawtoothUtils.calculateAddress(SawtoothUtils.FAMILY,publicKey);


            RemoteRESTTask task = RemoteRESTTask.Builder.newBuilder(RemoteRESTTask.TYPE.GET)
                .setHeader("x-mtini-apikey", this.retrieveAPIKeyFromPrefs())
                .setHost(host)
                .setPath("/api/v1/eatproxy")
                .setCustomerId(retrieveCustomerId())
                .setComponent("tenant")
                    .addQuery("dataAddress",dataAddress)
                //TODO implement http status listener incase of bad connectivity etc
                .build();
        task.execute();


            EATRequestResponseProtos.EATRequestResponse.Response response = task.get(10,TimeUnit.SECONDS);
            if( response==null) throw new RemoteDAOException("Null response");
            for(EstateAccountProtos.LedgerEntries.EstateModel estate:response.getEntries().getEstateDataList()){
                for(EstateAccountProtos.LedgerEntries.EstateModel.TenantModel tenant : estate.getTenantDataList()){
                    ret.add(toLocalModel(tenant));
                }

            }

        } catch (InterruptedException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network interruption error.");
        } catch (ExecutionException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote execution error.");
        } catch (TimeoutException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network timeout error.");
        } catch (InvalidProtocolBufferException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote data error.");
        } catch (RemoteDAOException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network security error.");
        }catch (AbstractDAOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RemoteDAOException("APIkey error");
        }catch (UnsupportedEncodingException  e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RemoteDAOException("Local security error.");
        }
        return ret;
    }


    private EstateModel toLocalModel(EstateAccountProtos.LedgerEntries.EstateModel estate) throws InvalidProtocolBufferException {
        EstateModel ret = null;

        String estateData = JsonFormat.printer().print(estate);
        Gson gson = new Gson();
        ret = gson.fromJson(estateData,EstateModel.class);

        return ret;
    }


    private TenantModel toLocalModel(EstateAccountProtos.LedgerEntries.EstateModel.TenantModel tenant) throws InvalidProtocolBufferException {
        TenantModel ret = null;

        String tenantData = JsonFormat.printer().print(tenant);
        Gson gson = new Gson();
        ret = gson.fromJson(tenantData,TenantModel.class);

        return ret;
    }

    private Wallet toLocalModel(MtiniWalletProtos.MtiniWallet wallet) throws InvalidProtocolBufferException {
        Wallet ret = null;

        String walletData = JsonFormat.printer().print(wallet);
        Gson gson = new Gson();
        ret = gson.fromJson(walletData,Wallet.class);

        return ret;
    }

    private MtiniWalletProtos.MtiniWallet toProtoModel(Wallet wallet) throws InvalidProtocolBufferException {
        Gson gson = new Gson();
        String objJson = gson.toJson(wallet);
        System.out.println("GSON => " + objJson);

        MtiniWalletProtos.MtiniWallet.Builder builder = MtiniWalletProtos.MtiniWallet.newBuilder();
        JsonFormat.parser().merge(objJson, builder);

        return builder.build();
    }

    private EstateAccountProtos.LedgerEntries.Builder toEntries(EstateModel o) throws InvalidProtocolBufferException, CloneNotSupportedException {
        EstateModel o2 = o.clone();
        String encodedId = o.getId();
        o2.setId(encodedId);

        Gson gson = new Gson();
        String objJson = gson.toJson(o2);
        System.out.println("GSON => " + objJson);
        EstateAccountProtos.LedgerEntries.EstateModel.Builder builder = EstateAccountProtos.LedgerEntries.EstateModel.newBuilder();
        JsonFormat.parser().merge(objJson, builder);
        EstateAccountProtos.LedgerEntries.EstateModel estate = builder.build();
        EstateAccountProtos.LedgerEntries.Builder  entries = EstateAccountProtos.LedgerEntries.newBuilder()
                .addEstateData(estate);
        //.setOperation(EstateAccountProtos.Operation.EDIT_ESTATE)
        //.build();
        return entries;
    }

    private EstateAccountProtos.LedgerEntries.Builder toEntries(TenantModel o) throws CloneNotSupportedException, InvalidProtocolBufferException {
        TenantModel o2 = o.clone();
        String encodedId = o.getId();
        o2.setId(encodedId);

        Gson gson = new Gson();
        String objJson = gson.toJson(o2);
        System.out.println("GSON => " + objJson);
        EstateAccountProtos.LedgerEntries.EstateModel.TenantModel.Builder builder = EstateAccountProtos.LedgerEntries.EstateModel.TenantModel.newBuilder();
        JsonFormat.parser().merge(objJson, builder);
        EstateAccountProtos.LedgerEntries.EstateModel.TenantModel tenant = builder.build();
        EstateAccountProtos.LedgerEntries.Builder  entries = EstateAccountProtos.LedgerEntries.newBuilder()
                .addEstateData(EstateAccountProtos.LedgerEntries.EstateModel.newBuilder().addTenantData(tenant));

        return entries;
    }


    public ArrayMap<String,List> getMyEstateAndTenantsList() throws RemoteDAOException {

        List<EstateModel> estates = new ArrayList<EstateModel>();
        List<TenantModel> tenants = new ArrayList<TenantModel>();

        try {
        RemoteRESTTask task = RemoteRESTTask.Builder.newBuilder(RemoteRESTTask.TYPE.GET)
                .setHeader("x-mtini-apikey", this.retrieveAPIKeyFromPrefs())
                .setHost(host)
                .setPath("/api/v1/eatproxy")
                .setCustomerId(retrieveCustomerId())
                .setComponent("tenant")
                //TODO implement http status listener incase of bad connectivity etc
                .build();
        task.execute();


            EATRequestResponseProtos.EATRequestResponse.Response response = task.get(10,TimeUnit.SECONDS);
            if( response==null) throw new RemoteDAOException("Null response");
            for(EstateAccountProtos.LedgerEntries.EstateModel estate:response.getEntries().getEstateDataList()){
                estates.add(toLocalModel(estate));
                for(EstateAccountProtos.LedgerEntries.EstateModel.TenantModel tenant : estate.getTenantDataList()){
                    tenants.add(toLocalModel(tenant));
                }

            }

        } catch (InterruptedException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network interruption error.");
        } catch (ExecutionException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote execution error.");
        } catch (TimeoutException e) {
            e.printStackTrace();
            Log.e(TAG,e.toString());
            throw new RemoteDAOException("Network timeout error.");
        } catch (InvalidProtocolBufferException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote data error.");
        } catch (RemoteDAOException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network security error.");
        }catch (AbstractDAOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RemoteDAOException("APIkey error");
        }

        ArrayMap<String,List> ret =  new ArrayMap<String,List>();
        ret.put("estates",estates);
        ret.put("tenants",tenants);

        return ret;
    }


    public ArrayMap<String,List<?>> getAccountState() throws RemoteDAOException {
        List<EstateModel> estates = new ArrayList<EstateModel>();
        List<TenantModel> tenants = new ArrayList<TenantModel>();

        try {

            Wallet wallet = getMyAuthedWallet();
            ECKey privateKey = ECKey.fromPrivate(wallet.getPrivateKeyBytes());
            privateKey.getPubKey();
            String publicKeyHex1 = privateKey.getPublicKeyAsHex();
            String publicKeyHex = wallet.getPublicKeyHex();
            byte[] publicKey = wallet.getPublicKeyUTF8Bytes();
            if(publicKeyHex1.equalsIgnoreCase(publicKeyHex)){
                System.out.println("Hooray");
            }else{
                System.out.println("Crap");
            }
            String dataAddress = SawtoothUtils.calculateAddress(SawtoothUtils.FAMILY,publicKey);

            RemoteRESTTask task = RemoteRESTTask.Builder.newBuilder(RemoteRESTTask.TYPE.GET)
                    .setHeader("x-mtini-apikey", this.retrieveAPIKeyFromPrefs())
                    .setHost(host)
                    .setPath("/api/v1/eatproxy")
                    .setCustomerId(retrieveCustomerId())
                    .setComponent("state")
                    .addQuery("dataAddress",dataAddress)
                    .setRemoteDAOListener(new
                                                  RemoteDAOListener() {
                                                      @Override
                                                      public void onRequestComplete(EATRequestResponseProtos.EATRequestResponse.Response response) {

                                                      }

                                                      @Override
                                                      public void onError(Throwable e) {
                                                          Log.e(TAG,e.getMessage(),e);

                                                          //DialogUtils.startErrorDialog(context,e.getMessage());

                                                          //DialogUtils.startErrorDialogRunnable((Activity)context,e.getMessage());
                                                      }
                                                  })
                    .build();
            task.execute();


            EATRequestResponseProtos.EATRequestResponse.Response response = task.get(120,TimeUnit.SECONDS);
            if( response==null) throw new RemoteDAOException("Null response");
            for(EstateAccountProtos.LedgerEntries.EstateModel estate : response.getEntries().getEstateDataList()){
                estates.add(toLocalModel(estate));
                for(EstateAccountProtos.LedgerEntries.EstateModel.TenantModel tenant : estate.getTenantDataList()){
                    tenants.add(toLocalModel(tenant));
                }

            }

        } catch (InterruptedException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network interruption error.");
        } catch (ExecutionException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote execution error.");
        } catch (TimeoutException e) {
            e.printStackTrace();
            Log.e(TAG,e.toString());
            throw new RemoteDAOException("Network timeout error.");
        } catch (InvalidProtocolBufferException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote data error.");
        } catch (RemoteDAOException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network security error.");
        }catch (AbstractDAOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RemoteDAOException("APIkey error");
        }catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RemoteDAOException("Local security error");
        }

        ArrayMap<String,List<?>> ret =  new ArrayMap<String,List<?>>();
        ret.put("estates",estates);
        ret.put("tenants",tenants);

        return ret;
    }


    @Override
    public String getImageById(String id) {
        throw new RuntimeException("Method not implemented.");
        //return null;
    }

    @Override
    public String[] getImageIdList(String modelId) {
        throw new RuntimeException("Method not implemented.");
        //return new String[0];
    }

    @Override
    public boolean deleteImage(String imageId) throws RemoteDAOException {


        try {

            EstateAccountProtos.LedgerEntries.ImageModel.Builder imageuilder = EstateAccountProtos.LedgerEntries.ImageModel.newBuilder();
            imageuilder.setId(ByteString.copyFrom(imageId.getBytes()));
            EstateAccountProtos.LedgerEntries entries = EstateAccountProtos.LedgerEntries.newBuilder()
                    .addImages(imageuilder)
                    .setOperation(EstateAccountProtos.Operation.DELETE_IMAGE)
                    .build();
            EATRequestResponseProtos.EATRequestResponse.Request request =  EATRequestResponseProtos.EATRequestResponse.Request.newBuilder().setEntries(entries).build();

            GenericRemoteRESTTask task = GenericRemoteRESTTask.Builder.newBuilder(GenericRemoteRESTTask.TYPE.POST)
                    .setHeader("x-mtini-apikey", this.retrieveAPIKeyFromPrefs())
                    .setHost(host)
                    .setPath("/api/v1/taesm")
                    .setCustomerId(retrieveCustomerId())
                    .setComponent("images")
                    .setComponentAction("delete_image")
                    .setRemoteDAOListener(new
                                                  RemoteDAOListener() {
                                                      @Override
                                                      public void onRequestComplete(EATRequestResponseProtos.EATRequestResponse.Response response) {

                                                      }

                                                      @Override
                                                      public void onError(Throwable e) {
                                                          Log.e(TAG,e.getMessage(),e);
                                                          //DialogUtils.startErrorDialog(context,e.getMessage());
                                                      }
                                                  })
                    //TODO implement http status listener incase of bad connectivity etc
                    .build();

            task.execute(request);


            EATRequestResponseProtos.EATRequestResponse.Response response = task.get(10,TimeUnit.SECONDS);
            if( response==null) throw new RemoteDAOException("Null response");

            JSONArray jsonArray = new JSONObject(response.getJsonResponse()).getJSONArray("elastic");
            if(jsonArray.length()>0) {
                JSONObject json = jsonArray.getJSONObject(0);
                String jsonResult = json.getString("result");
                if (jsonResult.equalsIgnoreCase("updated") || jsonResult.equalsIgnoreCase("created"))
                    return true;
            }

        } catch (InterruptedException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network interruption error.");
        } catch (ExecutionException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote execution error.");
        } catch (TimeoutException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network timeout error.");
        } catch (RemoteDAOException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network security error.");
        } catch (JSONException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Local data format error.");
        }catch (AbstractDAOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RemoteDAOException("APIkey error");
        }
        return false;
    }

    @Override
    public boolean addImage(String imageId, String modelId, String encodedImage) throws RemoteDAOException {

        List<TenantModel> ret = new ArrayList<TenantModel>();

        try {

            EstateAccountProtos.LedgerEntries.ImageModel.Builder imagebuilder = EstateAccountProtos.LedgerEntries.ImageModel.newBuilder();
            imagebuilder.setId(ByteString.copyFrom(imageId.getBytes()));
            imagebuilder.setModelId(ByteString.copyFrom(modelId.getBytes()));
            imagebuilder.setEncodedBitmap(encodedImage);

            EstateAccountProtos.LedgerEntries entries = EstateAccountProtos.LedgerEntries.newBuilder()
                    .addImages(imagebuilder)
                    .setOperation(EstateAccountProtos.Operation.ADD_IMAGE)
                    .build();
            EATRequestResponseProtos.EATRequestResponse.Request request =  EATRequestResponseProtos.EATRequestResponse.Request.newBuilder().setEntries(entries).build();

            GenericRemoteRESTTask task = GenericRemoteRESTTask.Builder.newBuilder(GenericRemoteRESTTask.TYPE.POST)
                    .setHeader("x-mtini-apikey", this.retrieveAPIKeyFromPrefs())
                    .setHost(host)
                    .setPath("/api/v1/taesm")
                    .setCustomerId(retrieveCustomerId())
                    .setComponent("images")
                    .setComponentAction("add_image")
                    .setRemoteDAOListener(new
                                                  RemoteDAOListener() {
                                                      @Override
                                                      public void onRequestComplete(EATRequestResponseProtos.EATRequestResponse.Response response) {

                                                      }

                                                      @Override
                                                      public void onError(Throwable e) {
                                                          Log.e(TAG,e.getMessage(),e);
                                                          //DialogUtils.startErrorDialog(context,e.getMessage());
                                                      }
                                                  })
                    //TODO implement http status listener incase of bad connectivity etc
                    .build();

            task.execute(request);


            EATRequestResponseProtos.EATRequestResponse.Response response = task.get(20,TimeUnit.SECONDS);
            if( response==null) throw new RemoteDAOException("Null response");
            JSONArray jsonArray = new JSONObject(response.getJsonResponse()).getJSONArray("elastic");
            if(jsonArray.length()>0) {
                JSONObject json = jsonArray.getJSONObject(0);
                String jsonResult = json.getString("result");
                if (jsonResult.equalsIgnoreCase("updated") || jsonResult.equalsIgnoreCase("created"))
                    return true;

            }
        } catch (InterruptedException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network interruption error.");
        } catch (ExecutionException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote execution error.");
        } catch (TimeoutException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network timeout error.");
        } catch (RemoteDAOException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network security error.");
        } catch (JSONException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Local data format error.");
        }catch (AbstractDAOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RemoteDAOException("APIkey error");
        }
        return false;
    }

    public void getMyImages(RemoteDAOListener listener) throws RemoteDAOException {

        try {
            RemoteRESTTask task = RemoteRESTTask.Builder.newBuilder(RemoteRESTTask.TYPE.GET)
                    .setHeader("x-mtini-apikey", this.retrieveAPIKeyFromPrefs())
                    .setHost(host)
                    .setPath("/api/v1/taesm")
                    .setCustomerId(retrieveCustomerId())
                    .setComponent("images")
                    .setRemoteDAOListener(listener)
                    .build();
            task.execute();

        } catch (RemoteDAOException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network security error. "+e.getMessage());
        }catch (AbstractDAOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RemoteDAOException("APIkey error");
        }

    }

    public Map<String,Map<String,String>> getMyImages() throws RemoteDAOException {

        Map<String,Map<String,String>> ret = new HashMap<String,Map<String,String>>();
        //List<ImagesModel> retList = new ArrayList<ImagesModel>();
        try {
            RemoteRESTTask task = RemoteRESTTask.Builder.newBuilder(RemoteRESTTask.TYPE.GET)
                    .setHeader("x-mtini-apikey", this.retrieveAPIKeyFromPrefs())
                    .setHost(host)
                    .setPath("/api/v1/taesm")
                    .setCustomerId(retrieveCustomerId())
                    .setComponent("images")

                    //TODO implement http status listener incase of bad connectivity etc
                    .build();
            task.execute();


            EATRequestResponseProtos.EATRequestResponse.Response response = task.get(10,TimeUnit.SECONDS);
            if( response==null) throw new RemoteDAOException("Null response");
            EstateAccountProtos.LedgerEntries entries = response.getEntries();
            if(entries!=null){
               for(EstateAccountProtos.LedgerEntries.ImageModel imageModel : entries.getImagesList()){

                   //String imageModelString = JsonFormat.printer().print(imageModel);
                   //JSONObject json = new JSONObject(imageModelString);
                   String modelId = imageModel.getModelId().toString();
                   String id = imageModel.getId().toString();
                   String image = imageModel.getEncodedBitmap();
                   Map<String,String> modelMap = null;

                   if(ret.containsKey(modelId)){
                       modelMap = ret.get(modelId);
                   }else{
                       modelMap = new HashMap<String,String>();
                   }
                   modelMap.put(id,image);
                   ret.put(modelId,modelMap);
               }

            }
           // JSONArray jsonArray = new JSONArray(response.getJsonResponse());


        } catch (InterruptedException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network interruption error.");
        } catch (ExecutionException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote execution error.");
        } catch (TimeoutException e) {
            e.printStackTrace();
            Log.e(TAG,e.toString());
            throw new RemoteDAOException("Network timeout error.");
        } catch (RemoteDAOException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network security error.");
        }catch (AbstractDAOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RemoteDAOException("APIkey error");
        }


        return ret;
    }



    public boolean requestEmailTextToken(SecurityModel sModel, RemoteDAOListener listener) throws RemoteDAOException {

        Gson gson = new Gson();
        String objJson = gson.toJson(sModel);

        EATRequestResponseProtos.EATRequestResponse.Request request =  EATRequestResponseProtos.EATRequestResponse.Request.newBuilder().setJsonRequest(objJson).build();

        GenericRemoteRESTTask task = GenericRemoteRESTTask.Builder.newBuilder(GenericRemoteRESTTask.TYPE.POST)
                .setHost(host)
                .setPath("/api/v1/security")
                .setComponent("token")
                .setComponentAction("request")
                //TODO implement http status listener incase of bad connectivity etc
                .setRemoteDAOListener(listener)
                .build();
        task.execute(request);

        /*
        EATRequestResponseProtos.EATRequestResponse.Response response = null;
        try {
            response = task.get(20,TimeUnit.SECONDS);
            if(response==null)throw new RemoteDAOException("An server side error occurred. Remote service not reachable.");
        } catch (InterruptedException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network interruption error.");
        } catch (ExecutionException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Remote execution error.");
        } catch (TimeoutException e) {
            Log.e(TAG,e.toString());
            throw new RemoteDAOException("Network timeout error.");
        }  catch (RemoteDAOException e) {
            Log.e(TAG,e.getMessage());
            throw new RemoteDAOException("Network security error: "+e.getMessage());
        }

        //String jsonRes = response==null?null:response.getJsonResponse();

        if( response==null) throw new RemoteDAOException("Null response");

       JSONArray jsonArray = new JSONObject(response.getJsonResponse()).getJSONArray("elastic");
        if(jsonArray.length()>0) {
            JSONObject json = jsonArray.getJSONObject(0);
            String jsonResult = json.getString("result");
            if (jsonResult.equalsIgnoreCase("updated") || jsonResult.equalsIgnoreCase("created"))
                return true;

        }*/

        return true;
    }


    public Wallet retrieveMyWallet(String address, SecurityModel sModel, @Nullable  String ... jwt) throws RemoteDAOException {
        Wallet ret = null;

        Gson gson = new Gson();
        String objJson = gson.toJson(sModel);

        EATRequestResponseProtos.EATRequestResponse.Request request =  EATRequestResponseProtos
                .EATRequestResponse
                .Request
                .newBuilder()
                .setJsonRequest(objJson)
                .build();

        try {
            String apijwt = jwt==null?this.retrieveAPIKeyFromPrefs():jwt[0];

            GenericRemoteRESTTask task = GenericRemoteRESTTask.Builder.newBuilder(GenericRemoteRESTTask.TYPE.POST)
                    .setHeader("x-mtini-apikey", apijwt)
                    .setHost(host)
                    .setPath("/api/v1/taesm")
                    //.setCustomerId(retrieveCustomerId())
                    .setComponent("wallet")
                    .addParam("address",address,true)
                    //.addQuery("phoneNumber",strip(sModel.phoneNumber))
                    //.addQuery("email",sModel.email)
                    //.addQuery("walletAddress",address)
                    //.addParam()
                    //TODO implement http status listener incase of bad connectivity etc
                    .build();
            task.execute(request);


            EATRequestResponseProtos.EATRequestResponse.Response response = task.get(10,TimeUnit.SECONDS);
           if( response==null || response.getJsonResponse()==null) throw new RemoteDAOException("Null response");
                JSONObject resObj = new JSONObject(response.getJsonResponse());
                if(resObj.has("error") ){
                String errorMsg = resObj.getString("error");
                throw new RemoteDAOException("Wallet could not be found using email and phone and password provided due to"+errorMsg);

            }else{

                    ByteString data = response.getData();
                if (data == null) {

                    throw new RemoteDAOException("Wallet could not be found using email and phone and password provided.");

                }


                MtiniWalletProtos.MtiniWallet mtiniWallet = MtiniWalletProtos.MtiniWallet.parseFrom(data);
                ret = toLocalModel(mtiniWallet);
            }


        } catch (InterruptedException e) {
            Log.e(TAG,e.getMessage(),e);
            throw new RemoteDAOException("Network interruption error.");
        } catch (ExecutionException e) {
            Log.e(TAG,e.getMessage(),e);
            throw new RemoteDAOException("Remote execution error.");
        } catch (TimeoutException e) {
            e.printStackTrace();
            Log.e(TAG,e.getMessage(),e);
            throw new RemoteDAOException("Network timeout error.");
        } catch (InvalidProtocolBufferException e) {
            Log.e(TAG,e.getMessage(),e);
            throw new RemoteDAOException("Remote data error.");
        } catch (RemoteDAOException e) {
            Log.e(TAG,e.getMessage(),e);
            throw new RemoteDAOException(e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG,e.getMessage(),e);
            throw new RemoteDAOException("Wallet security error.");
        }catch (AbstractDAOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RemoteDAOException("APIkey error");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RemoteDAOException("Input Data format error. Contact admin.");
        }


        return ret;
    }


    public boolean updateWallet(String address, String oldAddress, SecurityModel sModel, Wallet wallet, String action, @Nullable  String ... jwt) throws RemoteDAOException {

        try{

            //String address = Wallet.generateWalletAddress(sModel);
            //String suffix = Wallet.generateAddressSuffix(sModel);
            /*String suffix = "";
            try {
                suffix = Wallet.generateAddressSuffix(sModel);
            }catch(Exception e){}
            */

            String apijwt = jwt==null?this.retrieveAPIKeyFromPrefs():jwt[0];

            ByteString data  = toProtoModel(wallet).toByteString();

            EATRequestResponseProtos.EATRequestResponse.Request request =  EATRequestResponseProtos.EATRequestResponse.Request.newBuilder().setData(data).build();

            GenericRemoteRESTTask task = GenericRemoteRESTTask.Builder.newBuilder(GenericRemoteRESTTask.TYPE.POST)
                .setHeader("x-mtini-apikey", apijwt)
                .setHost(host)
                .setPath("/api/v1/taesm")
                //.setCustomerId(retrieveCustomerId())
                .setComponent("wallet")
                .setComponentAction(action)
                    .addQuery("userName",encode(sModel.getUserName()))
                    .addQuery("phoneNumber", encode(sModel.getPhoneNumber()))
                    .addQuery("email",encode(sModel.getEmail()))
                    .addQuery("walletAddress",encode(address))
                    //.addQuery("addressSuffix",suffix)
                    .addQuery("oldWalletAddress",encode(oldAddress))
                .setRemoteDAOListener(new
                                              RemoteDAOListener() {
                                                  @Override
                                                  public void onRequestComplete(EATRequestResponseProtos.EATRequestResponse.Response response) {

                                                  }

                                                  @Override
                                                  public void onError(Throwable e) {
                                                      Log.e(TAG,e.getMessage(),e);
                                                      //DialogUtils.startErrorDialog( context,e.getMessage());
                                                  }
                                              })

                .build();

        task.execute(request);

        EATRequestResponseProtos.EATRequestResponse.Response response = task.get(10,TimeUnit.SECONDS);
        if( response==null) throw new RemoteDAOException("Null response ");
            JSONArray jsonArray = new JSONObject(response.getJsonResponse()).getJSONArray("elastic");

            if(jsonArray.length()>0) {

            JSONObject json = jsonArray.getJSONObject(0);
            String jsonResult = json.getString("result");

            if (jsonResult.equalsIgnoreCase("updated") || jsonResult.equalsIgnoreCase("created"))
                return true;
        }

    } catch (InterruptedException e) {
        Log.e(TAG,e.getMessage(),e);
        throw new RemoteDAOException("Network interruption error.");
    } catch (ExecutionException e) {
        Log.e(TAG,e.getMessage(),e);
        throw new RemoteDAOException("Remote execution error.");
    } catch (TimeoutException e) {
        Log.e(TAG,e.getMessage(),e);
        throw new RemoteDAOException("Network timeout error.");
    } catch (RemoteDAOException e) {
        Log.e(TAG,e.getMessage());
        throw new RemoteDAOException("Network security error.");
    } catch (JSONException e) {
        Log.e(TAG,e.getMessage(),e);
        throw new RemoteDAOException("Local data format error.");
    } catch (InvalidProtocolBufferException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RemoteDAOException("Remote data error.");
        } catch (AbstractDAOException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RemoteDAOException("APIkey error");
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage(), e);
            throw new RemoteDAOException("Data format error. Contact admin.");
        }
        return false;

    }

    public static String encode(String s) throws UnsupportedEncodingException {
        return s==null?null:URLEncoder.encode(s,"UTF-8");
    }

    public static String strip(String s){
        String ret = null;

        if(s!=null){
            ret=s.replaceAll(" ", "").toLowerCase();
        }
        return ret;
    }


    public  static class RemoteDAOException extends IOException {
        public RemoteDAOException(String s){
            super(s);
        }
    }

}




