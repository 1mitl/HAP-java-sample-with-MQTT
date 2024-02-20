package com.beowulfe.hap.sample;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class AuthState implements Serializable {
    private static final long serialVersionUID = 1L;
    final String mac;
    final BigInteger salt;
    final byte[] privateKey;
    final String setupId;
    final ConcurrentMap<String, byte[]> userKeyMap = new ConcurrentHashMap<>();
    String PIN;

    public AuthState(String _PIN, String _mac, BigInteger _salt, byte[] _privateKey, String _setupId) {
        PIN = _PIN;
        salt = _salt;
        privateKey = _privateKey;
        mac = _mac;
        setupId = _setupId;
    }

    @Override
    public String toString() {
        return "AuthState {" + '\n' +
                "PIN='" + PIN + '\n' +
                "salt=" + salt + '\n' +
                "privateKey=" + privateKey + '\n' +
                "mac='" + mac + '\n' +
                "setupId='" + setupId + '\n' +
                "userKeyMap=" + userKeyMap + '\n' +
                '}';
    }
}