package io.mtini.model;

import java.util.List;

import com.prelimtek.android.basecomponents.dao.BaseDAOInterface;
import com.prelimtek.utils.crypto.Wallet;
import com.prelimtek.utils.crypto.dao.CryptoDAOInterface;
import com.prelimtek.android.picha.dao.MediaDAOInterface;

public interface AppDAOInterface extends CryptoDAOInterface<SecurityModel,Wallet>,MediaDAOInterface,BaseDAOInterface {

    enum ImageTblDescription{

        KEY_ID("id","TEXT"),
        MODEL_ID("modelId","TEXT"),
        IMAGE("image","BLOB");

        String label;
        String type;
        ImageTblDescription(String label, String type){
            this.label = label;
            this.type = type;
        }

    }

    enum SecurityTblDescription{

        KEY_ID("id","TEXT"),
        SOURCE("source","TEXT"),
        TOKEN("token","TEXT"),
        PRIVATEKEY("privatekey","TEXT");

        String label;
        String type;
        SecurityTblDescription(String label, String type){
            this.label = label;
            this.type = type;
        }

    }

    enum EstateTblDescription{

        KEY_ID("id","TEXT"),
        KEY_NAME("name","TEXT"),
        ADDRESS("address","TEXT"),
        CONTACTS("contacts","TEXT"),
        DESCRIPTION("notes","TEXT"),
        CURRENCY("currency","TEXT"),
        TYPE("type","TEXT");

        String label;
        String type;
        EstateTblDescription(String label, String type){
            this.label = label;
            this.type = type;
        }

    }

    enum TenantTblDescription{

        KEY_ID("id","TEXT"),
        KEY_ESTATE_ID("estateid","TEXT"),
        KEY_NAME("name","TEXT"),
        DESCRIPTION("notes","TEXT"),
        BLDG_NUMBER("number","TEXT"),
        RENT_DUE("rentdue","INTEGER"),
        STATUS("status","TEXT"),
        RENT("rent","INTEGER"),
        BALANCE("balance","INTEGER"),
        CURRENCY("currency","TEXT"),
        CONTACTS("contacts","TEXT"),
        SCHEDULE("schedule","TEXT"),
        PAID_DATE("paidDate","DATE"),
        DUE_DATE("dueDate","DATE");

        String label;
        String type;
        TenantTblDescription(String label, String type){
            this.label = label;
            this.type = type;
        }

    }

    public  void close();

    public  EstateModel getEstateById(String id);

    public  TenantModel getTenantById(String id);

    public  List<EstateModel> getMyEstateList();

    public  List<TenantModel> getTenantList(EstateModel propertyInfo);

    public  EstateModel addEstate(EstateModel newProperty);

    public  TenantModel addTenant(TenantModel newtenant,EstateModel property);

    public  boolean deleteEstate(EstateModel Property);

    public  boolean deleteTenant(TenantModel tenant,EstateModel property);

    public  EstateModel updateEstate(EstateModel Property);

    public  TenantModel updateTenant(TenantModel tenant,EstateModel property);

    public  TenantModel updateTenant(TenantModel tenant);






}
