package com.phoenix.assetbe.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.phoenix.assetbe.core.auth.session.MyUserDetails;
import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.dto.UserInDTO;
import com.phoenix.assetbe.dto.UserOutDTO;
import com.phoenix.assetbe.model.user.Status;
import com.phoenix.assetbe.model.user.User;
import com.phoenix.assetbe.model.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class UserServiceTest extends DummyEntity {

    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Spy
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JavaMailSender javaMailSender;

    @Spy
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        bCryptPasswordEncoder = spy(BCryptPasswordEncoder.class);
        authenticationManager = mock(AuthenticationManager.class);
        javaMailSender = mock(JavaMailSender.class);
        objectMapper = spy(ObjectMapper.class);
        userService = new UserService(authenticationManager, javaMailSender, bCryptPasswordEncoder, userRepository);
    }

    /**
     * 마이페이지
     */
    @Test
    public void testCheckPasswordService() throws Exception {
        // given
        Long userId = 1L;
        UserInDTO.CheckPasswordInDTO checkPasswordInDTO = new UserInDTO.CheckPasswordInDTO();
        checkPasswordInDTO.setId(userId);
        checkPasswordInDTO.setPassword("1234");

        String requestBody = objectMapper.writeValueAsString(checkPasswordInDTO);
        System.out.println("request 테스트: " + requestBody);

        User 송재근 = newMockUser(1L, "송", "재근");
        when(userRepository.findById(userId)).thenReturn(Optional.of(송재근));

        MyUserDetails myUserDetails = new MyUserDetails(송재근);

        // when
        userService.checkPasswordService(checkPasswordInDTO, myUserDetails);

        // then
        assertTrue(bCryptPasswordEncoder.matches(checkPasswordInDTO.getPassword(), 송재근.getPassword()));
        verify(userRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testWithdrawService() throws Exception {
        // given
        Long userId = 1L;
        UserInDTO.WithdrawInDTO withdrawInDTO = new UserInDTO.WithdrawInDTO();
        withdrawInDTO.setMessage("아파서 쉽니다.");

        String requestBody = objectMapper.writeValueAsString(withdrawInDTO);
        System.out.println("request 테스트: " + requestBody);

        User 송재근 = newMockUser(1L, "송", "재근");
        when(userRepository.findById(userId)).thenReturn(Optional.of(송재근));

        MyUserDetails myUserDetails = new MyUserDetails(송재근);

        // when
        userService.withdrawService(userId, withdrawInDTO, myUserDetails);

        // then
        assertEquals(withdrawInDTO.getMessage(), 송재근.getReason());
        assertEquals(Status.INACTIVE, 송재근.getStatus());
    }

    @Test
    public void testUpdateService() throws Exception {
        // given
        Long userId = 1L;

        UserInDTO.UpdateInDTO updateInDTO = new UserInDTO.UpdateInDTO();
        updateInDTO.setFirstName("송");
        updateInDTO.setLastName("재근");
        updateInDTO.setNewPassword("5678");

        String requestBody = objectMapper.writeValueAsString(updateInDTO);
        System.out.println("request 테스트: " + requestBody);

        User 송재근 = newMockUser(1L, "송", "재근");
        userRepository.save(송재근);

        MyUserDetails myUserDetails = new MyUserDetails(송재근);

        // stub 1
        when(userRepository.findById(userId)).thenReturn(Optional.of(송재근));

        // when
        userService.updateService(userId, updateInDTO, myUserDetails);

        // then
        Assertions.assertThat(bCryptPasswordEncoder.matches(updateInDTO.getNewPassword(), 송재근.getPassword()));
    }

    @Test
    public void testFindMyInfoService() throws Exception {
        // given
        Long userId = 1L;

        User 송재근 = newMockUser(1L, "송", "재근");


        when(userRepository.findById(any())).thenReturn(Optional.of(송재근));

        MyUserDetails myUserDetails = new MyUserDetails(송재근);

        // when
        UserOutDTO.FindMyInfoOutDTO findMyInfoOutDTO = userService.findMyInfoService(userId, myUserDetails);

        // then
        verify(userRepository, times(1)).findById(userId);
    }
}
