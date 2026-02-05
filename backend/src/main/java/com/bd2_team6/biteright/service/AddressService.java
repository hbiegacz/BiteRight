package com.bd2_team6.biteright.service;

import com.bd2_team6.biteright.controllers.requests.create_requests.AddressCreateRequest;
import com.bd2_team6.biteright.controllers.requests.update_requests.AddressUpdateRequest;
import com.bd2_team6.biteright.entities.address.Address;
import com.bd2_team6.biteright.entities.address.AddressRepository;
import com.bd2_team6.biteright.entities.user.User;
import com.bd2_team6.biteright.entities.user.UserRepository;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Autowired
    public AddressService(UserRepository userRepository, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }

    public Set<Address> findAddressesByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getAddresses();
    }

    public Address createAddress(String username, AddressCreateRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        Address newAddress = new Address();
        newAddress.setAddress(request.getAddress());
        newAddress.setCity(request.getCity());
        newAddress.setPostalCode(request.getPostalCode());
        newAddress.setCountry(request.getCountry());

        newAddress.setUser(user);
        user.getAddresses().add(newAddress);

        return addressRepository.save(newAddress);
    }

    public Address updateAddress(String username, AddressUpdateRequest request, Long addressId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Long userId = user.getId();
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address with provided id not found"));

        if (address.getUser().getId().equals(userId)) {
            address.setAddress(request.getAddress());
            address.setCity(request.getCity());
            address.setPostalCode(request.getPostalCode());
            address.setCountry(request.getCountry());
            addressRepository.save(address);
        }
        else {
            throw new IllegalArgumentException("Address with provided id does not belong to user");
        }
        
        return address;
    }

    public void deleteAddressById(String username, Long addressId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Long userId = user.getId();
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("Address with provided id not found"));

        if (address.getUser().getId().equals(userId)) {
            addressRepository.delete(address);
        }
        else {
            throw new IllegalArgumentException("Address with provided id does not belong to user");
        }
    }
}
