package com.example.ZAuth.Cache;

import com.example.ZAuth.Helper.ClientData;

import java.util.HashMap;

public class ClientIdCache {
    static HashMap<String, ClientData> clientIdmap;

    static {
        clientIdmap=new HashMap<>();
    }
}
