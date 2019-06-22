package io.mtini.android.http;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;
import cz.msebera.android.httpclient.entity.ContentType;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicHeader;
import io.mtini.model.RemoteDAOListener;
import io.mtini.proto.EATRequestResponseProtos;

/**
 * @author kaniundungu
 *
 *
 * The remote REST client for data interaction with backend processes
 * i.e. data retrieval and persistence.
 *
 *
 * Requirements: protobuf object EATRequestResponseProtos.EATRequestResponse which contains request and
 * response handles.
 *
 *
 * */
public class GenericRemoteRESTTask extends AsyncTask<EATRequestResponseProtos.EATRequestResponse.Request,Integer,EATRequestResponseProtos.EATRequestResponse.Response> {

    public static String TAG = GenericRemoteRESTTask.class.getSimpleName();

    public enum TYPE{POST,GET}

    TYPE _type;
    String _host;
    String _action;
    String _customerId;
    String _component = "estate";
    String _path = "/api/v1/taesm";


   public static class Builder{

       HashMap<String,Object> _param = new HashMap<String,Object>();
       HashMap<String,Object> _query = new HashMap<String,Object>();
       String _host;
       String _action;
       String _customerId;
       String _component = "estate";
       Header _header;

       String _path = "/api/v1/taesm";
       TYPE _type;
       private RemoteDAOListener _listener ;

       private Builder(TYPE type){
           _type = type;
       }


        public Builder setComponentAction(String action){
            _action = action;
            return this;
        }

        public Builder setComponent(String component){
            _component = component;
            return this;
        }

        public Builder setCustomerId(String customerId){
            _customerId = customerId;
            return this;
        }

        public Builder setPath(String path){
            _path = path;
            return this;
        }

       public Builder setHeader(String key, String value){
           _header = new BasicHeader(key,value);
           return this;
       }

       public Builder setHeader(Header header){
           _header = header;
           return this;
       }

        public Builder setHost(String host){
            _host = host;
            return this;
        }

       public Builder addParam(String key, Object val, boolean encoded)throws UnsupportedEncodingException{
           if(encoded) {
               _param.put(key, URLEncoder.encode(val.toString(),"UTF-8"));
           }else{
               _param.put(key, val);

           }
           return this;
       }

       public Builder addQuery(String key, Object val, boolean encoded) throws UnsupportedEncodingException {
           if(encoded){
               _query.put(key, URLEncoder.encode(val.toString(),"UTF-8"));
           }else {
               _query.put(key, val);
           }
           return this;
       }

        public Builder addParam(String key, Object val){
            _param.put(key, val);
           return this;
        }

       public Builder addQuery(String key, Object val){
           _query.put(key, val);
           return this;
       }

        public Builder setRemoteDAOListener(RemoteDAOListener listener){
            _listener = listener;
            return this;
        }

        public GenericRemoteRESTTask build(){
            GenericRemoteRESTTask ret = new GenericRemoteRESTTask();
            ret._host = _host;
            _path = _path+"/"+_component;
            _path = _customerId==null||_customerId.isEmpty()?_path: _path+"/"+_customerId;
            _path = _action==null||_action.isEmpty()?_path:_path+"/"+_action;

            Iterator<String> keys = _param.keySet().iterator();
            if(!_param.isEmpty()) {
                StringBuilder pathParam = new StringBuilder();
                pathParam.append("/");
                while (keys.hasNext()) {
                    String key = keys.next();
                    pathParam.append(key).append("/").append(_param.get(key));
                }
                _path = _path + pathParam;
            }


            if(!_query.isEmpty()) {
                StringBuilder query = new StringBuilder();
                query.append("?");
                keys = _query.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    query.append(key).append("=").append(_query.get(key)).append("&");
                }
                //_param.entrySet().stream().forEach(e->query.append(e.getKey()).append("=").append(e.getValue()).append(";"));
                _path = _path + query;
            }

            ret._path = _path;
            ret._header = _header;
            ret._listener=_listener;
            ret._type = _type;
            return ret;
        }

        public static Builder newBuilder(GenericRemoteRESTTask.TYPE  type){return new Builder(type);}
    }

    private GenericRemoteRESTTask(){}

    private RemoteDAOListener _listener ;

    private Header _header;

    @Override
    protected EATRequestResponseProtos.EATRequestResponse.Response doInBackground(EATRequestResponseProtos.EATRequestResponse.Request ... requests) {

        HttpClient httpClient = new DefaultHttpClient();

        HttpEntity entity = null;

        EATRequestResponseProtos.EATRequestResponse inRes = null;

        try {

            String url = _host + _path;

            if(_type.equals(TYPE.POST)){

                    entity = post(httpClient, url, requests[0]);

                    inRes = EATRequestResponseProtos.EATRequestResponse.parseFrom(entity.getContent());

                    System.out.println(inRes.toString());

                    if (_listener != null)
                        _listener.onRequestComplete(inRes.getResponse());


            }else  if(_type.equals(TYPE.GET)){

                entity = get(httpClient, url );

                inRes = EATRequestResponseProtos.EATRequestResponse.parseFrom(entity.getContent());

                System.out.println(inRes.toString());

                if (_listener != null)
                    _listener.onRequestComplete(inRes.getResponse());

            }

        } catch (IOException e) {

            Log.e(TAG, e.getMessage());

            e.printStackTrace();

            if (_listener != null)
                _listener.onError(e);

        }finally {
            ((DefaultHttpClient) httpClient).close();
        }


        return inRes==null?null:inRes.getResponse();
    }

    private HttpEntity post(HttpClient httpClient, String url, EATRequestResponseProtos.EATRequestResponse.Request request) throws IOException {

        HttpResponse httpResponse = null;

        EATRequestResponseProtos.EATRequestResponse reqRes = EATRequestResponseProtos.EATRequestResponse.newBuilder()
                .setRequest(request).build();

        byte[] byteArray = reqRes.toByteArray();//data.toByteArray();

        HttpPost httpPostRequest = new HttpPost(url);
        httpPostRequest.setEntity(new ByteArrayEntity(byteArray));
        httpPostRequest.addHeader(_header);

        httpPostRequest.setHeader("Accept", ContentType.APPLICATION_OCTET_STREAM.getMimeType());
        httpPostRequest.setHeader("Content-Type", ContentType.APPLICATION_OCTET_STREAM.getMimeType());

        httpResponse = httpClient.execute(httpPostRequest);

        Log.d(TAG,httpResponse.getStatusLine().toString());
        int status = httpResponse.getStatusLine().getStatusCode();
        if(status<200 || status>299 ){
            if (_listener != null)
                _listener.onError(new IOException(httpResponse.getStatusLine().toString()));
        }

        HttpEntity entity = httpResponse.getEntity();

        return entity;
    }

    private HttpEntity get(HttpClient httpClient, String url) throws IOException {

        HttpResponse httpResponse = null;
        HttpGet httpGetRequest = new HttpGet(url);
        httpGetRequest.addHeader(_header);
        httpGetRequest.setHeader("Accept", ContentType.APPLICATION_OCTET_STREAM.getMimeType());
        httpGetRequest.setHeader("Content-Type", ContentType.APPLICATION_OCTET_STREAM.getMimeType());

        httpResponse = httpClient.execute(httpGetRequest);

        Log.d(TAG,httpResponse.getStatusLine().toString());
        int status = httpResponse.getStatusLine().getStatusCode();
        if(status<200 || status>299 ){
            if (_listener != null)
                _listener.onError(new IOException(httpResponse.getStatusLine().toString()));
        }

        HttpEntity entity = httpResponse.getEntity();

        return entity;
    }

    /*
    @Override
    protected void onProgressUpdate(Integer... progress) {
        //setProgressPercent(progress[0]);
    }

    @Override
    protected void onPostExecute(EATRequestResponseProtos.EATRequestResponse.Response result) {
        //showDialog("Downloaded " + result + " bytes");
    }
    */

}
