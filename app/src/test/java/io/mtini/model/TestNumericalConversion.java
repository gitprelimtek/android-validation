package io.mtini.model;

import com.google.gson.Gson;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.UUID;

import io.mtini.proto.EstateAccountProtos;

public class TestNumericalConversion {


    @Test
    public void testBigDecimals() throws InvalidProtocolBufferException {

        TenantModel tenant = new TenantModel(UUID.randomUUID().toString(),UUID.randomUUID().toString(),"test tenant");

        tenant.setNotes("Something");
        tenant.setCurrency("KES");
        tenant.setContacts("5551212");
        tenant.setBuildingNumber("R1");
        tenant.setPaySchedule(TenantModel.SCHEDULE.monthly);
        tenant.setRent(BigDecimal.valueOf(1000.00));
        tenant.setDueDate(System.nanoTime());
        tenant.setBalance(BigDecimal.valueOf(1100.00));
        tenant.calculateStatus();


        System.out.println(tenant.toString());

        Gson gson = new Gson();
        String objJson = gson.toJson(tenant);
        System.out.println("GSON => " + objJson);

        EstateAccountProtos.LedgerEntries.EstateModel.TenantModel.Builder builder = EstateAccountProtos.LedgerEntries.EstateModel.TenantModel.newBuilder();
        JsonFormat.parser().merge(objJson, builder);
        EstateAccountProtos.LedgerEntries.EstateModel.TenantModel tenantProto = builder.build();


        System.out.println(tenantProto.toString());



    }
}
