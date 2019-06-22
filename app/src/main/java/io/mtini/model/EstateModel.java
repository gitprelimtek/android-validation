package io.mtini.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.google.common.base.Charsets;

import java.io.Serializable;
import java.util.ArrayList;
import android.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import io.mtini.android.tenantmanager.BR;

public class EstateModel extends BaseObservable implements Serializable, Cloneable{

    public static EstateModel create() {
        String id = Base64.encodeToString(UUID.randomUUID().toString().getBytes(Charsets.UTF_8),Base64.NO_WRAP);
        return new EstateModel(id,"","",null);
    }

    public enum TYPE{
        condo, apartment, house, commercial;
    static TYPE getType(String s){
        if(null!=s){
        switch(s) {
            case "condo":
                return condo;
            case "apartment":
                return apartment;
            case "house":
                return house;
            case "commercial":
                return commercial;
        }
        }
        return null;}
    }

    String id;
    String name;
    String address;
    TYPE type;
    String description;
    //Integer tenantCount;
    String contacts;
    String currency;

    public EstateModel(String id, String name, String address, TYPE type) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.type = type;
    }

    public EstateModel(String id, String name, String address, TYPE type, String contacts) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.type = type;
        this.contacts = contacts;
    }

    public EstateModel(String id, String name, String address, TYPE type, String contacts, String description) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.type = type;
        this.contacts = contacts;
        this.description = description;
    }

    public EstateModel(String id, String name, String address, TYPE type, String contacts, String description, String currency) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.type = type;
        this.contacts = contacts;
        this.description = description;
        this.currency = currency;
    }
    /*
    public EstateModel( String name, String address, TYPE type) {
        this.name = name;
        this.address = address;
        this.type = type;
    }
    */

    @Bindable
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        notifyPropertyChanged(BR.address);
    }

    @Bindable
    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
        notifyPropertyChanged(BR.type);
    }

    @Bindable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);
    }

    /*
    @Bindable
    public Integer getTenantCount() {
        return tenantCount;
    }

    public void setTenantCount(Integer tenantCount) {
        this.tenantCount = tenantCount;
        notifyPropertyChanged(BR.tenantCount);
    }
*/

    @Bindable
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency ) {
        this.currency = currency;
        notifyPropertyChanged(BR.currency);
    }

    @Bindable
    public String getContacts(){return contacts;}

    public void setContacts(String contacts){this.contacts=contacts;
        notifyPropertyChanged(BR.contacts);}

    public static String[] getEstateTypeItems(){
        List<String> ret = new ArrayList(EstateModel.TYPE.values().length);

        for(EstateModel.TYPE type : EstateModel.TYPE.values()){
            ret.add(type.name());
        }

        return ret.toArray(new String[ret.size()]);
    }


    @Override
    protected EstateModel clone() throws CloneNotSupportedException {
        return (EstateModel)super.clone();
    }

    public EstateModel createClone(){
        try
        {
            return this.clone();
        }catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int hashCode() {

        return Objects.hash( id, name,address,type,description,currency,contacts);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EstateModel)) return false;
        EstateModel that = (EstateModel) o;
        return
                Objects.equals(hashCode(), that.hashCode());
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append("id").append(":").append(id)
                .append("name").append(":").append(name)
                .append("address").append(":").append(address)
                .append("type").append(":").append(type)
                .append("notes").append(":").append(description)
                .append("currency").append(":").append(currency)
                .append("contacts").append(":").append(contacts);

        return builder.toString();
    }

}
