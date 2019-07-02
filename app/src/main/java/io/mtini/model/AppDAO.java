package io.mtini.model;

import android.content.Context;

import android.util.ArrayMap;
import android.util.Base64;
import android.util.Log;

import com.google.common.base.Charsets;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import com.prelimtek.android.basecomponents.dialog.DialogUtils;

import com.prelimtek.utils.crypto.Wallet;
import io.mtini.proto.EATRequestResponseProtos;
import io.mtini.proto.EstateAccountProtos;

/***/
public class AppDAO implements AppDAOInterface {

    LocalDAO localDao = null;
    RemoteDAO remoteDao = null;

    public static String TAG = Class.class.getSimpleName();

    private Context context;

    public static DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    protected AppDAO(Context context) {
        this.context = context;
    }

    protected AppDAO() {

    }

    private static AppDAO dao=null;

    public static AppDAO builder(){

        AppDAO dao = new AppDAO();

        return dao;
    }

    /*public static synchronized AppDAO open(Context context) throws SQLException {
        if(dao==null)
            dao = new AppDAO(context);

        return dao.open();
    }*/

    public AppDAO open(Context context) throws SQLException {

        localDao = LocalDAO
                .builder(context)
                .open();

        remoteDao = RemoteDAO
                .builder(context);

        return this;
    }

    public AppDAO open() throws SQLException {

        localDao = LocalDAO
                .builder(context)
                .open();

        remoteDao = RemoteDAO
                .builder(context);

        return this;
    }

    public void setContext(Context context){
        this.context = context;
    }

    public void close() {

        localDao.close();
    }

    public RemoteDAO getRemoteDao() {
        return remoteDao;
    }

    public LocalDAO getLocalDao() {
        return localDao;
    }

    @Override
    public EstateModel getEstateById(String id) {

        return localDao.getEstateById(id);
    }


    @Override
    public TenantModel getTenantById(String id) {

        return localDao.getTenantById(id);
    }

    @Override
    public List<EstateModel> getMyEstateList() {

        List<EstateModel>  ret = localDao.getMyEstateList();

        return ret;
    }

    @Override
    public List<TenantModel> getTenantList(EstateModel propertyInfo) {

        List<TenantModel>  ret = localDao.getTenantList(propertyInfo);

        return ret;
    }

    @Override
    public EstateModel addEstate(EstateModel newProperty) {
        EstateModel ret = null;
        try {
            ret = remoteDao.addEstate(newProperty);
        } catch (RemoteDAO.RemoteDAOException e) {
            Log.e(TAG,e.getMessage(),e);
            showErrorDialog(e.getMessage());
        }

        if(null!=ret)
            return localDao.addEstate(newProperty);
        else
            return null;
    }

    @Override
    public TenantModel addTenant(TenantModel newTenant, EstateModel estate) {

        TenantModel ret = null;
        try {
            ret = remoteDao.addTenant(newTenant,estate);
        } catch (RemoteDAO.RemoteDAOException e) {
            e.printStackTrace();
            showErrorDialog(e.getMessage());
        }

        if(null!= ret)
            return localDao.addTenant(newTenant,estate);
        else
            return null;

    }

    @Override
    public boolean deleteEstate(final EstateModel property) {

        try {
            remoteDao.deleteEstate(property);
        } catch (RemoteDAO.RemoteDAOException e) {
            showErrorDialog(e.getMessage());
            return false;
        }

        //delete images
        new Thread(new Runnable(){
            @Override
            public void run() {
                String[] imgIds = getImageIdList(property.getId());
                for(String imgId : imgIds){
                    deleteImage(imgId);
                }
            }
        }).start();


        return localDao.deleteEstate(property);
    }

    @Override
    public boolean deleteTenant(final TenantModel tenant, EstateModel property) {

        try {
            remoteDao.deleteTenant(tenant, property);
        } catch (RemoteDAO.RemoteDAOException e) {
            showErrorDialog(e.getMessage());
            return false;
        }

        //delete images
        new Thread(new Runnable(){
            @Override
            public void run() {
                String[] imgIds = getImageIdList(tenant.getId());
                for(String imgId : imgIds){
                    deleteImage(imgId);
                }
            }
        }).start();

        return localDao.deleteTenant(tenant, property);
    }

    @Override
    public EstateModel updateEstate(EstateModel estate) {

        EstateModel ret = null;
        try {
            ret = remoteDao.updateEstate(estate);
        } catch (RemoteDAO.RemoteDAOException e) {
            showErrorDialog(e.getMessage());
        }

        if(null!= ret)
            return localDao.updateEstate(estate);
        else
            return null;

    }

    @Override
    public TenantModel updateTenant(TenantModel newtenant) {
        TenantModel ret = null;
        try {
            ret = remoteDao.updateTenant(newtenant,null);
        } catch (RemoteDAO.RemoteDAOException e) {
            showErrorDialog(e.getMessage());
        }
            if(null != ret)
                return localDao.updateTenant(newtenant,null);
            else
                return null;
    }

    @Override
    public String getPrivateKey(String id) {
        return localDao.getPrivateKey(id);
    }

    @Override
    public String updatePrivateKey(String id, String privateKey) {
        return localDao.updatePrivateKey(id,privateKey);
    }

    @Deprecated
    @Override
    public void updateWallet(String address, String oldAddress, SecurityModel securityModel, Wallet wallet, String action) throws RemoteDAO.RemoteDAOException {


            remoteDao.updateWallet(address,oldAddress,securityModel,wallet,action);

            localDao.updateWallet(address,oldAddress, securityModel,wallet,action );

    }

    @Override
    public Wallet getMyWallet(String address, SecurityModel securityModel) throws RemoteDAO.RemoteDAOException{
        Wallet wallet = localDao.getMyWallet(address, securityModel);

        if(wallet==null)
            wallet = remoteDao.retrieveMyWallet(address,securityModel);

        //TODO validate remote wallet using hashcode
        //int hashcode = wallet.hashCode();

        return wallet;
    }

    @Override
    public String getImageById(String id) {
        return localDao.getImageById(id);

    }

    @Override
    public boolean deleteImage(String id) {
        try {
            if(remoteDao.deleteImage(id)) {
                localDao.deleteImage(id);
            }

        } catch (RemoteDAO.RemoteDAOException e) {
            Log.e(TAG,e.getMessage());
            showErrorDialog(e.getMessage());

        }
        return true;
    }

    @Override
    public String[] getImageIdList(String modelId) {

        return localDao.getImageIdList(modelId);
    }

    @Override
    public boolean addImage(String imageId, String modelId, String encodedImage) {
        boolean ret = true;
        try {
            if(remoteDao.addImage( imageId,  modelId,  encodedImage)){
                ret =  localDao.addImage(imageId,  modelId,  encodedImage);
            }
        } catch (RemoteDAO.RemoteDAOException e) {
            Log.e(TAG,e.getMessage());
            showErrorDialog(e.getMessage());
        }
        return ret;
    }

    @Deprecated
    @Override
    public TenantModel updateTenant(TenantModel newtenant, EstateModel estate) {

        return localDao.updateTenant(newtenant, estate);
    }


    public void uploadData()throws RemoteDAO.RemoteDAOException {

        ArrayMap<String,List> data = null;

            //data = getRemoteDao().getMyEstateAndTenantsList();
        data = getRemoteDao().getAccountState();


        List<EstateModel> estates = data.get("estates");
        List<TenantModel> tenants = data.get("tenants");

        for(EstateModel estate : estates){
            getLocalDao().addEstate(estate);
        }

        for(TenantModel tenant : tenants){
            getLocalDao().addTenant(tenant,null);
        }

        getRemoteDao().getMyImages(
                new RemoteDAOListener() {
                    @Override
                    public void onRequestComplete(EATRequestResponseProtos.EATRequestResponse.Response response) {
                        LocalDAO localDao = null;
                        try{
                            localDao = getLocalDao().open();

                        if( response==null) throw new RemoteDAO.RemoteDAOException("Null response");

                            EstateAccountProtos.LedgerEntries entries = response.getEntries();

                            if(entries!=null){

                                for(EstateAccountProtos.LedgerEntries.ImageModel imageModel : entries.getImagesList()) {

                                    String modelId = imageModel.getModelId().toStringUtf8();

                                    String id = imageModel.getId().toStringUtf8();

                                    String image = imageModel.getEncodedBitmap();

                                    localDao.addImage(id,modelId,image);
                                }

                            }

                        }catch(Exception err){
                            onError(err);
                        }finally {
                            if(localDao!=null)localDao.close();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        //do something throw e;
                        Log.e(TAG,e.getMessage());
                        //showErrorDialog(e.getMessage());//this is throwing error
                    }
                }
        );

    }
    //TEST DATA
    public void createData(){

        //TODO make a remote query to update local db.


        String encodedId = Base64.encodeToString(UUID.randomUUID().toString().getBytes(Charsets.UTF_8),Base64.NO_WRAP);
        addEstate(new EstateModel( encodedId,"KN Apartment","Kiambu Rd",EstateModel.TYPE.apartment));

        encodedId = Base64.encodeToString(UUID.randomUUID().toString().getBytes(Charsets.UTF_8),Base64.NO_WRAP);

        EstateModel offices = new EstateModel( encodedId,"KN Plaza","Kiambu Rd",EstateModel.TYPE.commercial);
        addEstate(offices);

        try {

            //addTenant(new TenantModel(Base64.getMimeEncoder().encodeToString(UUID.randomUUID().toString().getBytes(Charsets.UTF_8)),"Mtu Moja", "N1", "Lounge hall", Timestamps.fromMillis(dateFormat.parse("2018/11/01").getTime()), TenantModel.STATUS.paid, BigDecimal.valueOf(10000.00), BigDecimal.valueOf(0.00), "0708555121"), offices);
            //addTenant(new TenantModel(Base64.getMimeEncoder().encodeToString(UUID.randomUUID().toString().getBytes(Charsets.UTF_8)),"Mtu Mwingine", "N3", "Office 1", Timestamps.fromMillis(dateFormat.parse("2018/11/01").getTime()), TenantModel.STATUS.paid, BigDecimal.valueOf(3000), BigDecimal.valueOf(0), "0708555222"), offices);
            //addTenant(new TenantModel(Base64.getMimeEncoder().encodeToString(UUID.randomUUID().toString().getBytes(Charsets.UTF_8)),"Watu Wengi", "N4", "Office 2", Timestamps.fromMillis(dateFormat.parse("2018/11/01").getTime()), TenantModel.STATUS.paid, BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), "0708555444"), offices);
            Long time = Calendar.getInstance(Locale.US).getTimeInMillis();
            addTenant(new TenantModel(Base64.encodeToString(UUID.randomUUID().toString().getBytes(Charsets.UTF_8),Base64.NO_WRAP),encodedId,"Mtu Moja", "N1", "Lounge hall", time, TenantModel.STATUS.paid, BigDecimal.valueOf(10000.00), BigDecimal.valueOf(0.00), "0708555121"), offices);
            addTenant(new TenantModel(Base64.encodeToString(UUID.randomUUID().toString().getBytes(Charsets.UTF_8),Base64.NO_WRAP),encodedId,"Mtu Mwingine", "N3", "Office 1", time, TenantModel.STATUS.paid, BigDecimal.valueOf(3000), BigDecimal.valueOf(0), "0708555222"), offices);
            addTenant(new TenantModel(Base64.encodeToString(UUID.randomUUID().toString().getBytes(Charsets.UTF_8),Base64.NO_WRAP),encodedId,"Watu Wengi", "N4", "Office 2", time, TenantModel.STATUS.paid, BigDecimal.valueOf(1000), BigDecimal.valueOf(1000), "0708555444"), offices);

        }catch(Exception e){
            throw new RuntimeException(e.getMessage(),e);
        }
    }

    private void showErrorDialog(String message){
        //DialogUtils.startErrorDialogRunnable((Activity) context,message);
        DialogUtils.startErrorDialog( context,message);
    }
}
