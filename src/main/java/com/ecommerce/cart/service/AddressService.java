package com.ecommerce.cart.service;

import com.ecommerce.cart.model.Address;
import com.ecommerce.cart.pattern.singleton.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddressService {
    private List<Address> addresses;
    private Logger logger;
    private static final String ADDRESS_FILE = "addresses.dat";
    
    public AddressService() {
        this.logger = Logger.INSTANCE;
        this.addresses = loadAddresses();
        if (addresses == null) {
            this.addresses = new ArrayList<>();
        }
    }
    
    public void addAddress(String name, String phone, String province, String city, String district, String detailAddress, boolean isDefault) {
        String id = UUID.randomUUID().toString();
        Address address = new Address(id, name, phone, province, city, district, detailAddress, isDefault);
        
        // 如果设置为默认地址，将其他地址设为非默认
        if (isDefault) {
            for (Address addr : addresses) {
                addr.setDefault(false);
            }
        }
        
        addresses.add(address);
        saveAddresses();
        logger.log("Added address: " + address.toString());
    }
    
    public void updateAddress(String id, String name, String phone, String province, String city, String district, String detailAddress, boolean isDefault) {
        for (Address address : addresses) {
            if (address.getId().equals(id)) {
                // 如果设置为默认地址，将其他地址设为非默认
                if (isDefault) {
                    for (Address addr : addresses) {
                        addr.setDefault(false);
                    }
                }
                
                address.setName(name);
                address.setPhone(phone);
                address.setProvince(province);
                address.setCity(city);
                address.setDistrict(district);
                address.setDetailAddress(detailAddress);
                address.setDefault(isDefault);
                
                saveAddresses();
                logger.log("Updated address: " + address.toString());
                break;
            }
        }
    }
    
    public void deleteAddress(String id) {
        addresses.removeIf(address -> address.getId().equals(id));
        saveAddresses();
        logger.log("Deleted address with id: " + id);
    }
    
    public List<Address> getAddresses() {
        return addresses;
    }
    
    public Address getDefaultAddress() {
        for (Address address : addresses) {
            if (address.isDefault()) {
                return address;
            }
        }
        return addresses.isEmpty() ? null : addresses.get(0);
    }
    
    private void saveAddresses() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ADDRESS_FILE))) {
            oos.writeObject(addresses);
            logger.log("Addresses saved to file");
        } catch (IOException e) {
            logger.log("Failed to save addresses: " + e.getMessage());
        }
    }
    
    private List<Address> loadAddresses() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ADDRESS_FILE))) {
            @SuppressWarnings("unchecked")
            List<Address> loadedAddresses = (List<Address>) ois.readObject();
            logger.log("Addresses loaded from file");
            return loadedAddresses;
        } catch (FileNotFoundException e) {
            logger.log("Address file not found, creating new address list");
        } catch (IOException | ClassNotFoundException e) {
            logger.log("Failed to load addresses: " + e.getMessage());
        }
        return new ArrayList<>();
    }
}