package com.bd2_team6.biteright.address;

import com.bd2_team6.biteright.entities.address.Address;
import com.bd2_team6.biteright.entities.user.User;
import com.bd2_team6.biteright.entities.address.AddressRepository;
import com.bd2_team6.biteright.entities.user.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class AddressTests {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void shouldSaveAddress() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        Address address = new Address(user, "123 Test St", "Test City", "12345", "Test Country");

        addressRepository.save(address);

        Address foundAddress = addressRepository.findById(address.getAddressId()).orElse(null);
        assertNotNull(foundAddress);
        assertEquals(address.getCity(), foundAddress.getCity());
        assertEquals(address.getPostalCode(), foundAddress.getPostalCode());
        assertEquals(address.getCountry(), foundAddress.getCountry());
        assertEquals(address.getUser().getUsername(), foundAddress.getUser().getUsername());
    }

    @Test
    public void shouldUpdateAddress() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        Address address = new Address(user, "123 Test St", "Test City", "12345", "Test Country");
        addressRepository.save(address);

        address.setCity("New Test City");
        addressRepository.save(address);

        Address updatedAddress = addressRepository.findById(address.getAddressId()).orElse(null);
        assertNotNull(updatedAddress);
        assertEquals("New Test City", updatedAddress.getCity());
    }

    @Test
    public void shouldDeleteAddress() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        Address address = new Address(user, "123 Test St", "Test City", "12345", "Test Country");
        addressRepository.save(address);

        addressRepository.delete(address);

        Address deletedAddress = addressRepository.findById(address.getAddressId()).orElse(null);
        assertNull(deletedAddress);
    }

    @Test
    public void shouldFindAddressByUser() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");
        userRepository.save(user);

        Address address1 = new Address(user, "123 Test St", "Test City", "12345", "Test Country");
        Address address2 = new Address(user, "456 Another St", "Another City", "67890", "Another Country");
        addressRepository.save(address1);
        addressRepository.save(address2);

        entityManager.flush();
        entityManager.clear();

        User refreshedUser = userRepository.findById(user.getId()).orElse(null);
        assertNotNull(refreshedUser);

        Set<Address> addresses = refreshedUser.getAddresses();
        assertEquals(2, addresses.size());
        assertTrue(addresses.stream().anyMatch(a -> a.getAddress().equals("123 Test St")));
        assertTrue(addresses.stream().anyMatch(a -> a.getAddress().equals("456 Another St")));
    }

    @Test
    public void shouldNotSave2SameAddresses() {
        User user = new User("john_doe", "john@example.com", "passwordHash", "standard");

        Address address1 = new Address(user, "123 Test St", "Test City", "12345", "TestCountry");
        Address address2 = new Address(user, "123 Test St", "Test City", "12345", "TestCountry"); // ten sam adres

        user.getAddresses().add(address1);
        user.getAddresses().add(address2);

        userRepository.save(user);

        User saved = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(1, saved.getAddresses().size());
    }

    @Test
    public void shouldDeleteAddressesWhenUserDeleted() {
        User user = new User("jane_doe", "jane@example.com", "password", "standard");

        Address address1 = new Address(user, "A St", "City", "123", "PL");
        Address address2 = new Address(user, "B St", "City", "456", "PL");

        user.getAddresses().add(address1);
        user.getAddresses().add(address2);

        userRepository.save(user);

        Long userId = user.getId();
        Long address1Id = address1.getAddressId();
        Long address2Id = address2.getAddressId();

        userRepository.deleteById(userId);

        assertFalse(userRepository.findById(userId).isPresent());
        assertFalse(addressRepository.findById(address1Id).isPresent());
        assertFalse(addressRepository.findById(address2Id).isPresent());
    }
}
