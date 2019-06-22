package io.mtini.model;

import io.mtini.proto.EATRequestResponseProtos;

public interface RemoteDAOListener {

    void onRequestComplete(EATRequestResponseProtos.EATRequestResponse.Response response);

    void onError(Throwable e);

}
