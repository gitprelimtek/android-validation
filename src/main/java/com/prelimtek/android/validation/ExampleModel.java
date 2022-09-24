package com.prelimtek.android.validation;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.io.Serializable;
import java.math.BigDecimal;

public class ExampleModel extends BaseObservable implements Serializable{



    BigDecimal rentDue = new BigDecimal(0);

    @Bindable
    public BigDecimal getRentDue() {
        return rentDue;
    }

    public void setRentDue(BigDecimal rentDue) {
        this.rentDue = rentDue;
        notifyPropertyChanged(BR.rentDue);
    }
}
