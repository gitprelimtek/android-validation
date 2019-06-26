package io.mtini.model;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.prelimtek.android.basecomponents.Configuration;

import com.prelimtek.utils.crypto.Wallet;

public class LocalDAO extends AbstractDAO implements AppDAOInterface{

    public static String DATABASE_NAME = "EstateDatabase";
    public static int DATABASE_VERSION = 1;
    public static String TAG = "EstateAdaptor";
    public static String ESTATE_TABLE = "Estate";
    public static String TENANT_TABLE = "Tenant";
    public static String SECURITY_TABLE = "Security";
    public static String IMAGE_TABLE = "Images";

    DBHelper mDbHelper;

    private LocalDAO(Context context) {
        super(context);
    }

    private LocalDAO() {
        super();
    }

    public static LocalDAO builder(Context context){

        LocalDAO dao = new LocalDAO(context);

        return dao;
    }

    public static LocalDAO builder(){

        LocalDAO dao = new LocalDAO();

        return dao;
    }

    public LocalDAO open() throws SQLException {
        mDbHelper = new DBHelper(context);
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        //if does not exisit , create
        mDbHelper.onCreate(database);
        return this;
    }

    public LocalDAO open(Context context) throws SQLException {
        super.context = context;
        mDbHelper = new DBHelper(context);
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        //if does not exisit , create
        mDbHelper.onCreate(database);
        return this;
    }

    public void close(){
        if (mDbHelper != null ){
            mDbHelper.close();
        }
    }


    @Override
    public void setContext(Context context){
        this.context = context;
    }

    @Override
    public EstateModel getEstateById(String id) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM "+ESTATE_TABLE+" where "+EstateTblDescription.KEY_ID.label+" = ? ", new String[]{id});
        EstateModel estateModel = null;
        int count = cursor.getCount();
        if(count >= 1){
            cursor.moveToNext();
            estateModel = new EstateModel(
                    //String id, String name, String address, TYPE type
                    cursor.getString(cursor.getColumnIndex(EstateTblDescription.KEY_ID.label)),
                    cursor.getString(cursor.getColumnIndex(EstateTblDescription.KEY_NAME.label)),
                    cursor.getString(cursor.getColumnIndex(EstateTblDescription.ADDRESS.label)),
                    EstateModel.TYPE.getType(cursor.getString(cursor.getColumnIndex(EstateTblDescription.TYPE.label))),
                    cursor.getString(cursor.getColumnIndex(EstateTblDescription.CONTACTS.label)),
                    cursor.getString(cursor.getColumnIndex(EstateTblDescription.DESCRIPTION.label)),
                    cursor.getString(cursor.getColumnIndex(EstateTblDescription.CURRENCY.label))
            );
            cursor.close();
            //database.close();
        }
        return estateModel;
    }


    @Override
    public TenantModel getTenantById(String id) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM "+TENANT_TABLE+" where "+TenantTblDescription.KEY_ID.label+" = ? ", new String[]{id});
        int count = cursor.getCount();
        TenantModel tenantModel = null;
        if(count >= 1){
            cursor.moveToNext();
            //Calendar time = Calendar.getInstance();
            //time.setTimeInMillis(Long.parseLong(cursor.getString(cursor.getColumnIndex(TenantTblDescription.RENT_DUE.label))));
            tenantModel = new TenantModel(
                    cursor.getString(cursor.getColumnIndex(TenantTblDescription.KEY_ID.label)),
                    cursor.getString(cursor.getColumnIndex(TenantTblDescription.KEY_ESTATE_ID.label)),
                    cursor.getString(cursor.getColumnIndex(TenantTblDescription.KEY_NAME.label)));


            tenantModel.setBuildingNumber( cursor.getString(cursor.getColumnIndex(TenantTblDescription.BLDG_NUMBER.label)));
            tenantModel.setNotes(      cursor.getString(cursor.getColumnIndex(TenantTblDescription.DESCRIPTION.label)));
            tenantModel.setStatus(     TenantModel.STATUS.getStatus(cursor.getString(cursor.getColumnIndex(TenantTblDescription.STATUS.label))));
            tenantModel.setRent(          BigDecimal.valueOf(cursor.getInt(cursor.getColumnIndex(TenantTblDescription.RENT.label))));
            tenantModel.setBalance(        BigDecimal.valueOf(cursor.getInt(cursor.getColumnIndex(TenantTblDescription.BALANCE.label))));
            tenantModel.setContacts(        cursor.getString(cursor.getColumnIndex(TenantTblDescription.CONTACTS.label)));
            tenantModel.setCurrency(        cursor.getString(cursor.getColumnIndex(TenantTblDescription.CURRENCY.label)));
            tenantModel.setRentDue(        BigDecimal.valueOf(cursor.getInt(cursor.getColumnIndex(TenantTblDescription.RENT_DUE.label))));
            tenantModel.setPaySchedule(        TenantModel.SCHEDULE.getSchedule(cursor.getString(cursor.getColumnIndex(TenantTblDescription.SCHEDULE.label))));
            tenantModel.setPaidDate(        cursor.getLong(cursor.getColumnIndex(TenantTblDescription.PAID_DATE.label)));
            tenantModel.setDueDate(        cursor.getLong(cursor.getColumnIndex(TenantTblDescription.DUE_DATE.label)));



            cursor.close();
            //database.close();
        }
        return tenantModel;
    }

    @Override
    public List<EstateModel> getMyEstateList() {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM "+ESTATE_TABLE, null);
        int count = cursor.getCount();
        List<EstateModel> ret = new ArrayList<EstateModel>(count);
        if(count >= 1){
            while(cursor.moveToNext()) {
                ret.add (new EstateModel(
                        //String id, String name, String address, TYPE type
                        cursor.getString(cursor.getColumnIndex(EstateTblDescription.KEY_ID.label)),
                        cursor.getString(cursor.getColumnIndex(EstateTblDescription.KEY_NAME.label)),
                        cursor.getString(cursor.getColumnIndex(EstateTblDescription.ADDRESS.label)),
                        EstateModel.TYPE.getType(cursor.getString(cursor.getColumnIndex(EstateTblDescription.TYPE.label))),
                        cursor.getString(cursor.getColumnIndex(EstateTblDescription.CONTACTS.label)),
                        cursor.getString(cursor.getColumnIndex(EstateTblDescription.DESCRIPTION.label)),
                        cursor.getString(cursor.getColumnIndex(EstateTblDescription.CURRENCY.label))
                ));
            };
        };
        cursor.close();
        //database.close();
        //mDbHelper.close();
        return ret;
    }

    @Override
    public List<TenantModel> getTenantList(EstateModel propertyInfo) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM "+TENANT_TABLE+" where "+TenantTblDescription.KEY_ESTATE_ID.label+" = ?", new String[]{propertyInfo.id+""});
        int count = cursor.getCount();
        List<TenantModel> ret = new ArrayList<TenantModel>(count);
        if(count >= 1){
            TenantModel tenantModel;
            while(cursor.moveToNext()) {
                //Calendar time = Calendar.getInstance();
                //time.setTimeInMillis(Long.parseLong(cursor.getString(cursor.getColumnIndex(TenantTblDescription.RENT_DUE.label))));

                try {


                     tenantModel = new TenantModel(
                            cursor.getString(cursor.getColumnIndex(TenantTblDescription.KEY_ID.label)),
                            cursor.getString(cursor.getColumnIndex(TenantTblDescription.KEY_ESTATE_ID.label)),
                            cursor.getString(cursor.getColumnIndex(TenantTblDescription.KEY_NAME.label)));


                    tenantModel.setBuildingNumber( cursor.getString(cursor.getColumnIndex(TenantTblDescription.BLDG_NUMBER.label)));
                    tenantModel.setNotes(      cursor.getString(cursor.getColumnIndex(TenantTblDescription.DESCRIPTION.label)));
                    tenantModel.setStatus(     TenantModel.STATUS.getStatus(cursor.getString(cursor.getColumnIndex(TenantTblDescription.STATUS.label))));
                    tenantModel.setRent(          BigDecimal.valueOf(cursor.getInt(cursor.getColumnIndex(TenantTblDescription.RENT.label))));
                    tenantModel.setBalance(        BigDecimal.valueOf(cursor.getInt(cursor.getColumnIndex(TenantTblDescription.BALANCE.label))));
                    tenantModel.setContacts(        cursor.getString(cursor.getColumnIndex(TenantTblDescription.CONTACTS.label)));
                    tenantModel.setCurrency(        cursor.getString(cursor.getColumnIndex(TenantTblDescription.CURRENCY.label)));
                    tenantModel.setRentDue(        BigDecimal.valueOf(cursor.getInt(cursor.getColumnIndex(TenantTblDescription.RENT_DUE.label))));
                    tenantModel.setPaySchedule(        TenantModel.SCHEDULE.getSchedule(cursor.getString(cursor.getColumnIndex(TenantTblDescription.SCHEDULE.label))));
                    tenantModel.setPaidDate(        cursor.getLong(cursor.getColumnIndex(TenantTblDescription.PAID_DATE.label)));
                    tenantModel.setDueDate(        cursor.getLong(cursor.getColumnIndex(TenantTblDescription.DUE_DATE.label)));

                    ret.add(tenantModel);
                }catch(Exception e){throw new RuntimeException(e.getMessage(),e);}
            };
        };
        cursor.close();
        //database.close();
        //mDbHelper.close();
        return ret;
    }

    @Override
    public EstateModel addEstate(EstateModel newProperty) {

        //write to remote first then to local

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        ContentValues content = new ContentValues();
        content.put(EstateTblDescription.KEY_ID.label, newProperty.id);
        content.put(EstateTblDescription.KEY_NAME.label, newProperty.name);
        content.put(EstateTblDescription.ADDRESS.label, newProperty.address);
        content.put(EstateTblDescription.CONTACTS.label, newProperty.contacts);
        content.put(EstateTblDescription.TYPE.label, newProperty.type!=null?newProperty.type.name():null);
        content.put(EstateTblDescription.DESCRIPTION.label, newProperty.getDescription());
        content.put(EstateTblDescription.CURRENCY.label, newProperty.currency);

        long rowId = database.insert(ESTATE_TABLE, null, content);
        //database.close();

        if(rowId==-1)return null;//failed

        return newProperty;
    }

    @Override
    public TenantModel addTenant(TenantModel newtenant, EstateModel estate) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        ContentValues content = new ContentValues();

        content.put(TenantTblDescription.KEY_ID.label, newtenant.id.toString());
        content.put(TenantTblDescription.KEY_NAME.label, newtenant.name);

        if(estate!=null)
            content.put(TenantTblDescription.KEY_ESTATE_ID.label, estate.id.toString());
        else
            content.put(TenantTblDescription.KEY_ESTATE_ID.label, newtenant.estateId.toString());

        content.put(TenantTblDescription.CONTACTS.label, newtenant.contacts);
        content.put(TenantTblDescription.BALANCE.label, newtenant.balance==null?0:newtenant.balance.intValue());
        content.put(TenantTblDescription.RENT.label, newtenant.rent==null?0:newtenant.rent.intValue());

        content.put(TenantTblDescription.STATUS.label, newtenant.status==null?null:newtenant.status.name());

        content.put(TenantTblDescription.BLDG_NUMBER.label, newtenant.buildingNumber);
        content.put(TenantTblDescription.DESCRIPTION.label, newtenant.getNotes());
        content.put(TenantTblDescription.CURRENCY.label, newtenant.currency);
        content.put(TenantTblDescription.RENT_DUE.label, newtenant.rentDue==null?0:newtenant.rentDue.intValue());
        content.put(TenantTblDescription.SCHEDULE.label, newtenant.paySchedule==null?null:newtenant.paySchedule.name());
        content.put(TenantTblDescription.PAID_DATE.label, newtenant.paidDate);
        content.put(TenantTblDescription.DUE_DATE.label, newtenant.dueDate);

        long rowId = database.insert(TENANT_TABLE, null, content);

        //database.close();

        if(rowId==-1)return null;//failed

        //newtenant.setId(rowId);

        return newtenant;
    }

    @Override
    public boolean deleteEstate(EstateModel property) {
        int doneDelete = 0;
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        doneDelete = database.delete(ESTATE_TABLE, " "+EstateTblDescription.KEY_ID.label+" = ? ", new String[]{property.id.toString()});
        Log.w(TAG, Integer.toString(doneDelete));
        //database.close();
        return doneDelete > 0;
    }

    @Override
    public boolean deleteTenant(TenantModel tenant, EstateModel property) {
        int doneDelete = 0;
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        doneDelete = database.delete(TENANT_TABLE, " "+TenantTblDescription.KEY_ESTATE_ID.label+" = ? AND "+TenantTblDescription.KEY_ID.label+" = ? ", new String[]{tenant.getEstateId().toString(), tenant.id.toString()});
        Log.w(TAG, Integer.toString(doneDelete));
        //database.close();
        return doneDelete > 0;
    }

    @Override
    public EstateModel updateEstate(EstateModel property) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        ContentValues content = new ContentValues();

        //content.put(EstateTblDescription.KEY_ID.label, property.id.toString());
        content.put(EstateTblDescription.KEY_NAME.label, property.name);
        content.put(EstateTblDescription.ADDRESS.label, property.address);
        content.put(EstateTblDescription.CONTACTS.label, property.contacts);
        content.put(EstateTblDescription.TYPE.label, property.type==null?null:property.type.name());
        content.put(EstateTblDescription.DESCRIPTION.label, property.getDescription());
        content.put(EstateTblDescription.CURRENCY.label, property.currency);
        int rowsaffected = database.update(ESTATE_TABLE, content, " "+EstateTblDescription.KEY_ID.label+" = ? ", new String[]{property.id.toString()});

        //database.close();

        if(rowsaffected!=1)return null;

        return property;
    }

    @Override
    public TenantModel updateTenant(TenantModel newtenant) {
        return updateTenant(newtenant,null);
    }

    @Override
    public TenantModel updateTenant(TenantModel newtenant, EstateModel estate) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        ContentValues content = new ContentValues();

        content.put(TenantTblDescription.KEY_ID.label, newtenant.id.toString());
        content.put(TenantTblDescription.KEY_NAME.label, newtenant.name);

        if(estate!=null)
            content.put(TenantTblDescription.KEY_ESTATE_ID.label, estate.id.toString());
        else
            content.put(TenantTblDescription.KEY_ESTATE_ID.label, newtenant.estateId.toString());

        content.put(TenantTblDescription.CONTACTS.label, newtenant.contacts);
        content.put(TenantTblDescription.BALANCE.label, newtenant.balance==null?0:newtenant.balance.intValue());
        content.put(TenantTblDescription.RENT.label, newtenant.rent==null?0:newtenant.rent.intValue());


        content.put(TenantTblDescription.STATUS.label, newtenant.status==null?null:newtenant.status.name());
        content.put(TenantTblDescription.CURRENCY.label, newtenant.currency);

        content.put(TenantTblDescription.BLDG_NUMBER.label, newtenant.buildingNumber);
        content.put(TenantTblDescription.DESCRIPTION.label, newtenant.getNotes());

        content.put(TenantTblDescription.RENT_DUE.label, newtenant.rentDue==null?0:newtenant.rentDue.intValue());
        content.put(TenantTblDescription.SCHEDULE.label, newtenant.paySchedule==null?null:newtenant.paySchedule.name());
        content.put(TenantTblDescription.PAID_DATE.label, newtenant.paidDate);
        content.put(TenantTblDescription.DUE_DATE.label, newtenant.dueDate);

        long rowId = database.update(TENANT_TABLE,  content, " "+TenantTblDescription.KEY_ID.label+" = ? ", new String[]{newtenant.id.toString()} );
        //database.close();

        if(rowId==-1)return null;//failed

        return newtenant;
    }

    @Override
    public String getPrivateKey(String id){
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM "+SECURITY_TABLE+" where "+SecurityTblDescription.KEY_ID.label+" = ?", new String[]{id});
        int count = cursor.getCount();
        String privateKey = null;
        if(count >= 1){
            while(cursor.moveToNext()) {
                privateKey = cursor.getString(cursor.getColumnIndex(SecurityTblDescription.PRIVATEKEY.label));
            }
        }
        cursor.close();
        //database.close();
        return privateKey;

    }

    @Override
    public String updatePrivateKey(String id, String privateKey){

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        ContentValues content = new ContentValues();

        content.put(SecurityTblDescription.KEY_ID.label, id);
        content.put(SecurityTblDescription.PRIVATEKEY.label, privateKey);

        if(getPrivateKey(id)!=null) {

            int rowsaffected = database.update(SECURITY_TABLE, content, " " + SecurityTblDescription.KEY_ID.label + " = ? ", new String[]{id});
            //database.close();
            if(rowsaffected!=1)return null;
            return privateKey;
        }else{

            long rowId = database.insert(SECURITY_TABLE, null, content);
            //database.close();
            if(rowId<0)return null;
            return privateKey;

        }


    }

    @Override
    public String getImageById(String id) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT "+ImageTblDescription.IMAGE.label+" FROM "+IMAGE_TABLE+" where "+ImageTblDescription.KEY_ID.label+" = ?", new String[]{id});
        int count = cursor.getCount();
        String encodeImage = null;
        if(count >= 1){
            while(cursor.moveToNext()) {
                encodeImage = cursor.getString(cursor.getColumnIndex(ImageTblDescription.IMAGE.label));
            }
        }
        cursor.close();
        //database.close();
        return encodeImage;
    }

    @Override
    public boolean deleteImage(String id) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        int count = database.delete(IMAGE_TABLE,ImageTblDescription.KEY_ID.label+" = ?", new String[]{id});
        //database.close();
        return true;
    }

    @Override
    public String[] getImageIdList(String modelId) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT "+ImageTblDescription.KEY_ID.label+" FROM "+IMAGE_TABLE+" where "+ImageTblDescription.MODEL_ID .label+" = ?", new String[]{modelId});
        int count = cursor.getCount();
        String[] ret = new String[count];
        int i = 0;
        String encodeImage = null;
        if(count >= 1){
            while(cursor.moveToNext()) {
                encodeImage = cursor.getString(cursor.getColumnIndex(ImageTblDescription.KEY_ID.label));
                ret[i++]=encodeImage;
            }
        }
        cursor.close();
        //database.close();
        return ret;
    }

    public int updateImage(String id, String modelId) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        ContentValues content = new ContentValues();

        content.put(ImageTblDescription.MODEL_ID.label, modelId);
        int rowsAffected = database.update(IMAGE_TABLE, content," " + ImageTblDescription.KEY_ID.label + " = ? ", new String[]{id});
        //database.close();

        return rowsAffected;
    }


    @Override
    public boolean addImage(String imageId, String modelId, String encodedImage) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        ContentValues content = new ContentValues();

        content.put(ImageTblDescription.KEY_ID.label, imageId);
        content.put(ImageTblDescription.MODEL_ID.label, modelId);
        content.put(ImageTblDescription.IMAGE.label, encodedImage);

        long rowId = database.insert(IMAGE_TABLE, null, content);
        //database.close();
        if(rowId<0)return false;
        //return imageId;
        return true;
    }

    /**
     * Returns address
     * Do not create address because local Wallet
     * */
    @Override
    public void updateWallet(String address, String oldAddress, SecurityModel securityModel, Wallet wallet, String action) {
        String walletStr = new Gson().toJson(wallet).toString();
        //byte[] walletByte = wallet
        Configuration
                .configuredPreferences(context)
                .edit()
                .putString(address,walletStr)
                .commit();
    }

    /**
     * Do not create address because local Wallet */
    @Override
    public Wallet getMyWallet(String address, SecurityModel securityModel) {
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(Configuration.SERVER_SIDE_PREFERENCES_TAG, Context.MODE_PRIVATE);

        String json = pref.getString(address,null);
        if(json==null)return null;
       return new Gson().fromJson(json,Wallet.class);
    }

    public void dropTables(){
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        mDbHelper.onUpgrade(database,0,1);
    }

    private static final String ESTATE_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + ESTATE_TABLE + " (" +
            EstateTblDescription.KEY_ID.label + " "+EstateTblDescription.KEY_ID.type+" PRIMARY KEY," +
            EstateTblDescription.KEY_NAME.label + " "+EstateTblDescription.KEY_NAME.type+","+
            EstateTblDescription.CONTACTS.label + " "+EstateTblDescription.CONTACTS.type+","+
            EstateTblDescription.ADDRESS.label + " "+EstateTblDescription.ADDRESS.type+"," +
            EstateTblDescription.DESCRIPTION.label + " "+EstateTblDescription.DESCRIPTION.type+"," +
            EstateTblDescription.TYPE.label + " "+EstateTblDescription.TYPE.type+","+
            EstateTblDescription.CURRENCY.label + " "+EstateTblDescription.CURRENCY.type+");" ;

    private static final String TENANT_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + TENANT_TABLE + " (" +
            TenantTblDescription.KEY_ID.label + " "+TenantTblDescription.KEY_ID.type+" PRIMARY KEY," +
            TenantTblDescription.KEY_ESTATE_ID.label + " "+TenantTblDescription.KEY_ESTATE_ID.type+","+
            TenantTblDescription.KEY_NAME.label + " "+TenantTblDescription.KEY_NAME.type+","+
            TenantTblDescription.CONTACTS.label + " "+TenantTblDescription.CONTACTS.type+"," +
            TenantTblDescription.DESCRIPTION.label + " "+TenantTblDescription.DESCRIPTION.type+"," +
            TenantTblDescription.RENT.label + " "+TenantTblDescription.RENT.type+"," +
            TenantTblDescription.RENT_DUE.label + " "+TenantTblDescription.RENT_DUE.type+"," +
            TenantTblDescription.BALANCE.label + " "+TenantTblDescription.BALANCE.type+"," +
            TenantTblDescription.STATUS.label + " "+TenantTblDescription.STATUS.type+"," +
            TenantTblDescription.BLDG_NUMBER.label + " "+TenantTblDescription.BLDG_NUMBER.type+","+
            TenantTblDescription.CURRENCY.label + " "+TenantTblDescription.CURRENCY.type+","+
            TenantTblDescription.DUE_DATE.label + " "+TenantTblDescription.DUE_DATE.type+","+
            TenantTblDescription.PAID_DATE.label + " "+TenantTblDescription.PAID_DATE.type+","+
            TenantTblDescription.SCHEDULE.label + " "+TenantTblDescription.SCHEDULE.type+""+
            ");" ;


    private static final String SECURITY_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + SECURITY_TABLE + " (" +
            SecurityTblDescription.KEY_ID.label + " "+SecurityTblDescription.KEY_ID.type+" PRIMARY KEY," +
            SecurityTblDescription.SOURCE.label + " "+SecurityTblDescription.SOURCE.type+","+
            SecurityTblDescription.TOKEN.label + " "+SecurityTblDescription.TOKEN.type+","+
            SecurityTblDescription.PRIVATEKEY.label + " "+SecurityTblDescription.PRIVATEKEY.type+");" ;


    private static final String IMAGE_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS "
            + IMAGE_TABLE + " (" +
            ImageTblDescription.KEY_ID.label + " "+ImageTblDescription.KEY_ID.type+" PRIMARY KEY," +
            ImageTblDescription.MODEL_ID.label + " "+ImageTblDescription.MODEL_ID.type+","+
            ImageTblDescription.IMAGE.label + " "+ImageTblDescription.IMAGE.type+");" ;



    private static class DBHelper extends SQLiteOpenHelper {

        DBHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db){

            Log.w(TAG, IMAGE_TABLE_CREATE);
            db.execSQL(IMAGE_TABLE_CREATE);

            Log.w(TAG, SECURITY_TABLE_CREATE);
            db.execSQL(SECURITY_TABLE_CREATE);

            Log.w(TAG, ESTATE_TABLE_CREATE);
            db.execSQL(ESTATE_TABLE_CREATE);

            Log.w(TAG, TENANT_TABLE_CREATE);
            db.execSQL(TENANT_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

            db.execSQL("DROP TABLE IF EXISTS "+TENANT_TABLE);
            db.execSQL("DROP TABLE IF EXISTS "+ESTATE_TABLE);
            db.execSQL("DROP TABLE IF EXISTS "+IMAGE_TABLE);
            onCreate(db);
        }

    }


}
