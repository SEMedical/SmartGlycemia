package edu.tongji.backend.service.impl;

import edu.tongji.backend.clients.UserClient2;
import edu.tongji.backend.dto.DoctorInfoDTO;
import edu.tongji.backend.entity.Doctor;
import edu.tongji.backend.entity.User;
import edu.tongji.backend.mapper.DoctorMapper;
import edu.tongji.backend.mapper.HospitalMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {
    @Mock
    private DoctorMapper doctorMapper;

    @Mock
    private UserClient2 userClient2;

    @Mock
    private HospitalMapper hospitalMapper;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Test
    void testGetAccountList() {
        List<DoctorInfoDTO> expectedList = Arrays.asList(new DoctorInfoDTO(), new DoctorInfoDTO());
        when(doctorMapper.getAccountList()).thenReturn(expectedList);

        List<DoctorInfoDTO> result = accountService.getAccountList();

        assertEquals(expectedList, result);
        verify(doctorMapper, times(1)).getAccountList();
    }

    @Test
    public void testAddAccountSuccess() throws NoSuchAlgorithmException {
        Doctor doctor = new Doctor();
        doctor.setIdCard("123456789");
        String contact = "1234567890";
        String address = "Test Address";

        when(userClient2.getMaxUserId()).thenReturn(1);

        accountService.addAccount(doctor, contact, address);

        verify(userClient2, times(1)).addUser(any(User.class));
        verify(doctorMapper, times(1)).insert(doctor);
        assertNotNull(doctor.getDoctorId());
    }

    @Test
    public void testAddAccountException() throws NoSuchAlgorithmException {
        Doctor doctor = new Doctor();
        String contact = "1234567890";
        String address = "Test Address";

        when(userClient2.getMaxUserId()).thenThrow(new RuntimeException("UserClient2 error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountService.addAccount(doctor, contact, address));

        assertEquals("UserClient2 error", exception.getMessage());
        verify(doctorMapper, times(0)).insert(doctor);
    }

    @Test
    public void testDeleteAccountSuccess() {
        int doctorId = 1;

//        doNothing().when(doctorMapper).deleteById(doctorId);
        when(doctorMapper.deleteById(doctorId)).thenReturn(1);
        doNothing().when(userClient2).rmUser(doctorId);

        accountService.deleteAccount(doctorId);

        verify(doctorMapper, times(1)).deleteById(doctorId);
        verify(userClient2, times(1)).rmUser(doctorId);
    }

    @Test
    public void testDeleteAccountException() {
        int doctorId = 1;

        doThrow(new RuntimeException("Delete error")).when(doctorMapper).deleteById(doctorId);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountService.deleteAccount(doctorId));

        assertEquals("Delete error", exception.getMessage());

        verify(doctorMapper, times(1)).deleteById(doctorId);
        verify(userClient2, times(0)).rmUser(doctorId);
    }

    @Test
    public void testRepeatedIdCard() {
        String idCard = "123456";
        when(doctorMapper.repeatedIdCard(idCard)).thenReturn(true);

        Boolean result = accountService.repeatedIdCard(idCard);

        assertTrue(result);
        verify(doctorMapper, times(1)).repeatedIdCard(idCard);
    }
}