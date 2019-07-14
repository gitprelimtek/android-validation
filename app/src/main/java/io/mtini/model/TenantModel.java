package io.mtini.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.google.common.base.Charsets;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

import android.icu.util.Calendar;
import android.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import io.mtini.android.tenantmanager.BR;

public class TenantModel extends BaseObservable implements Serializable,Cloneable{


    public static TenantModel create(EstateModel estate) {

        String id = Base64.encodeToString(UUID.randomUUID().toString().getBytes(Charsets.UTF_8),Base64.NO_WRAP);

        TenantModel newTenant = new TenantModel(id,estate.getId(),"","","",new Date().getTime(),TenantModel.STATUS.new_tenant,null,null,"");

        return newTenant;
    }

    public enum STATUS {
        paid,late,balance,evicted,new_tenant;
        static STATUS getStatus(String s){
            if(null!=s){
            switch(s) {
                case "paid":
                    return paid;
                case "late":
                    return late;
                case "balance":
                    return balance;
                case "evicted":
                    return evicted;
                case "new_tenant":
                    return new_tenant;
                    default: return null;
            }
            }
            return null;}
    }

    public enum SCHEDULE {
        monthly,biweekly,weekly;
        static SCHEDULE getSchedule(String s){
            if(null!=s){
                switch(s) {
                    case "monthly":
                        return monthly;
                    case "biweekly":
                        return biweekly;
                    case "weekly":
                        return weekly;
                    default: return null;
                }
            }
            return null;}
    }

    String id;
    String estateId;
    String name;
    String buildingNumber;
    SCHEDULE paySchedule;
    BigDecimal rentDue = new BigDecimal(0);//this scheduled payment expected
    Long dueDate;//is calculated by paySchedule

    BigDecimal balance = new BigDecimal(0);//is payment overdue or overpayments indicated by +/-
    String contacts;
    Long paidDate = System.currentTimeMillis();
    BigDecimal rent = new BigDecimal(0);//this is actual pay on paidDate
    STATUS status;
    String notes;
    String currency;

    /**
     *     bytes id = 1;
     *     bytes estateId = 2;
     *     string name = 3;
     *     string buildingNumber = 4;
     *     string contacts = 5;
     *   	int64 rentDue = 6;
     *   	TenantStatus status = 7;
     *   	double rent = 8;
     *   	double balance = 9;
     *   	string notes = 10;
     *   	*/

    public TenantModel(String id, String estateId, String name){
        this.id = id;
        this.estateId=estateId;
        this.name = name;
    }

    public TenantModel(String id, String estateId, String name, String buildingNumber, String notes, Long dueDate, STATUS status, BigDecimal rent, BigDecimal balance, String contacts) {
        this.id = id;
        this.estateId=estateId;
        this.name = name;
        this.buildingNumber = buildingNumber;
        this.notes = notes;
        this.dueDate = dueDate;
        this.status = status;
        this.rent = rent;
        this.balance = balance;
        this.contacts = contacts;
    }

    public TenantModel(String id, String estateId, String name, String buildingNumber, String notes, Long dueDate, STATUS status, BigDecimal rent, BigDecimal balance, String contacts, String currency) {
        this.id = id;
        this.estateId=estateId;
        this.name = name;
        this.buildingNumber = buildingNumber;
        this.notes = notes;
        this.dueDate = dueDate;
        this.status = status;
        this.rent = rent;
        this.balance = balance;
        this.contacts = contacts;
        this.currency = currency;
    }


    public TenantModel(String id, String name, String buildingNumber, String notes, Long dueDate, STATUS status, BigDecimal rent, BigDecimal balance, String contacts) {
        this.id = id;
        this.name = name;
        this.buildingNumber = buildingNumber;
        this.notes = notes;
        this.dueDate = dueDate;
        this.status = status;
        this.rent = rent;
        this.balance = balance;
        this.contacts = contacts;
    }

    public TenantModel(String name, String buildingNumber, String notes, Long dueDate, STATUS status, BigDecimal rent, BigDecimal balance, String contacts) {
        this.name = name;
        this.buildingNumber = buildingNumber;
        this.notes = notes;
        this.dueDate = dueDate;
        this.status = status;
        this.rent = rent;
        this.balance = balance;
        this.contacts = contacts;
    }

    public TenantModel(String name, String buildingNumber, String notes, Long dueDate, STATUS status, BigDecimal rent, BigDecimal balance, String contacts, String currency) {
        this.name = name;
        this.buildingNumber = buildingNumber;
        this.notes = notes;
        this.dueDate = dueDate;
        this.status = status;
        this.rent = rent;
        this.balance = balance;
        this.contacts = contacts;
        this.currency = currency;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEstateId() {
        return estateId;
    }

    public void setEstateId(String estateId) {
        this.estateId = estateId;
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
    public String getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(String buildingNumber) {
        this.buildingNumber = buildingNumber;
        notifyPropertyChanged(BR.buildingNumber);
    }

    @Bindable
    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
        notifyPropertyChanged(BR.description);
    }

    @Bindable
    public BigDecimal getRentDue() {
        return rentDue;
    }

    public void setRentDue(BigDecimal rentDue) {
        this.rentDue = rentDue;
        notifyPropertyChanged(BR.rentDue);
    }

    @Bindable
    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
        notifyPropertyChanged(BR.status);
    }

    @Bindable
    public BigDecimal getRent() {
        return rent;
    }

    public void setRent(BigDecimal rent) {
        this.rent = rent;
        notifyPropertyChanged(BR.rent);
    }

    @Bindable
    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
        notifyPropertyChanged(BR.balance);
    }

    @Bindable
    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        if(this.contacts==null
                || !this.contacts.equals(contacts)) {
            this.contacts = contacts;
            notifyPropertyChanged(BR.contacts);
        }
    }

    @Bindable
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency ) {
        this.currency = currency;
        notifyPropertyChanged(BR.currency);
    }

    @Bindable
    public Long getPaidDate() {
        return paidDate;
    }

    public void setPaidDate(Long paidDate) {
        this.paidDate = paidDate;
        notifyPropertyChanged(BR.paidDate);
    }

    @Bindable
    public SCHEDULE getPaySchedule() {
        return paySchedule;
    }

    public void setPaySchedule(SCHEDULE paySchedule) {
        this.paySchedule = paySchedule;
        notifyPropertyChanged(BR.paySchedule);
    }

    @Bindable
    public Long getDueDate() {
        return dueDate;
    }

    public void setDueDate(Long dueDate) {
        this.dueDate = dueDate;
        notifyPropertyChanged(BR.dueDate);
    }

    public static String[] getScheduleItems(){
        List<String> ret = new ArrayList<String>(SCHEDULE.values().length);

        for(SCHEDULE schedule : SCHEDULE.values()){
            ret.add(schedule.name());
        }

        return ret.toArray(new String[ret.size()]);
    }

    public static String[] getStatusItems(){
        List<String> ret = new ArrayList<String>(STATUS.values().length);

        for(STATUS status : STATUS.values()){
            ret.add(status.name());
        }

        return ret.toArray(new String[ret.size()]);
    }

    @Override
    protected TenantModel clone() throws CloneNotSupportedException {
        return (TenantModel)super.clone();
    }

    public TenantModel createClone(){
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

        return Objects.hash( id, estateId, name,buildingNumber,notes,rentDue,status,rent,balance,currency,contacts, paySchedule,paidDate,dueDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TenantModel)) return false;
        TenantModel that = (TenantModel) o;
        return
                Objects.equals(hashCode(), that.hashCode());
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder = builder.append("id").append(id).append("\n").
                append("estateId").append(estateId).append("\n").
                append("name").append(name).append("\n").
                append("buildingNumber").append(buildingNumber).append("\n").
                append("notes").append(notes).append("\n").
                append("rentDue").append(rentDue).append("\n").
                append("status").append(status).append("\n").
                append("rent").append(rent).append("\n").
                append("balance").append(balance).append("\n").
                append("currency").append(currency).append("\n").
                append("contacts").append(contacts).append("\n").
                append("paySchedule").append(paySchedule).append("\n").
                append("dueDate").append(dueDate).append("\n").
                append("paidDate").append(paidDate).append("\n");

        return builder.toString();
    }

    public BigDecimal calculateBalance(){
        //return balance.add(rentDue.subtract(rent));
        return balance.subtract(rentDue.subtract(rent));
    }

    /**Is called after database confirmation.*/
    public void resetRentValue(){

        setRent(BigDecimal.valueOf(0));

    }

    public void calculateStatus(){

        long now = System.currentTimeMillis();

        balance = balance == null? new BigDecimal(0): balance;
        rent = rent == null? new BigDecimal(0): rent;

        if( now<=dueDate ){

            //balance = balance.subtract(rent);
            balance = balance.add(rent);

            if(balance.intValue()<0){
                status = status!=STATUS.late ? STATUS.balance : STATUS.late;
            } else {
                status = STATUS.paid;
            }

        }else{

            //balance = balance.add(rentDue);
            balance = balance.subtract(rentDue);

            if(dueDate<=paidDate){
                //nothing
            }else{
                status = STATUS.late;
            }
            dueDate = incrementByScheduleMillis(this.paySchedule,dueDate);
            calculateStatus();

        }

    }

    private Long incrementByScheduleMillis(SCHEDULE paySchedule, long date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);

        switch (paySchedule){
            case weekly:calendar.add(Calendar.WEEK_OF_YEAR,1);break;
            case biweekly:calendar.add(Calendar.WEEK_OF_YEAR,2);break;
            case monthly:calendar.add(Calendar.MONTH,1);break;
        }

        return calendar.getTimeInMillis() ;
    }


}
