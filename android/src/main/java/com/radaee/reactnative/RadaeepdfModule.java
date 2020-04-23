package com.radaee.reactnative;

import android.content.Context;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.radaee.util.RadaeePDFManager;

public class RadaeepdfModule extends ReactContextBaseJavaModule {
    private static final String REACT_CLASS = "Radaeepdf";
    private Context context;

    private RadaeePDFManager mPDFManager;

    public RadaeepdfModule(ReactApplicationContext reactContext) {
        super(reactContext); //required by React Native
        this.context = reactContext;
        mPDFManager = new RadaeePDFManager();
        mPDFManager.setDebugMode(false);
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @ReactMethod
    public void Activate(int licenseType, String companyName, String mail, String key, Callback result) {
        result.invoke(mPDFManager.activateLicense(this.context, licenseType, companyName, mail, key));
    }

    @ReactMethod
    public void Show(String path, String password) {
        mPDFManager.show(this.context, path, password);
    }

}