package com.phoenix.assetbe.controller;

import com.phoenix.assetbe.core.config.MyTestSetUp;
import com.phoenix.assetbe.core.dummy.DummyEntity;
import com.phoenix.assetbe.model.asset.*;
import com.phoenix.assetbe.model.user.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("에셋 컨트롤러 TEST")
@ActiveProfiles("test")
@Sql("classpath:db/teardown.sql")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
public class AssetControllerTest {

    private DummyEntity dummy = new DummyEntity();

    @Autowired
    private MyTestSetUp myTestSetUp;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() throws Exception {
        List<User> userList = myTestSetUp.saveUser();
        List<Asset> assetList = myTestSetUp.saveAsset();

        myTestSetUp.saveUserScenario(userList, assetList);
        myTestSetUp.saveCategoryAndSubCategoryAndTag(assetList);
    }

    @DisplayName("에셋 상세정보 비로그인 성공")
    @Test
    public void get_asset_details_test() throws Exception {
        // given
        Long id = 3L;

        // when
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                .get("/assets/{id}/details",id));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));
    }

    @DisplayName("에셋 상세정보 로그인 성공")
    @WithUserDetails(value = "양진호@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_asset_details_with_user_test() throws Exception {
        // given
        Long id = 1L;

        // when
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                .get("/assets/{id}/details",id));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));
    }

    @DisplayName("개별에셋 로그인 성공")
    @WithUserDetails(value = "양진호@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_assets_with_user_test() throws Exception {
        // given
        String page = "8";
        String size = "3";
        // when
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                .get("/assets")
                .param("page", page)
                .param("size", size));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));
    }

    @DisplayName("개별에셋 비로그인 성공")
    @Test
    public void get_assets_test() throws Exception {
        // given
        String page = "0";
        String size = "3";

        // when
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                .get("/assets")
                .param("page", page)
                .param("size", size));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));
    }

    @DisplayName("카테고리별 에셋 조회 로그인 성공")
    @WithUserDetails(value = "양진호@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void get_asset_list_with_user_id_and_pagination_by_category_test() throws Exception {
        // given
        String categoryName = "luxury";
        String page = "0";
        String size = "4";

        // when
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                .get("/assets/{categoryName}",categoryName)
                .param("page", page)
                .param("size", size));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));
    }

    @DisplayName("카테고리별 에셋 조회 비로그인 성공")
    @Test
    public void get_asset_list_with_pagination_by_category_test() throws Exception {
        // given
        String categoryName = "luxury";
        String page = "0";
        String size = "4";

        // when
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                .get("/assets/{categoryName}",categoryName)
                .param("page", page)
                .param("size", size));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));
    }

    @DisplayName("하위 카테고리별 에셋 조회 로그인 성공")
    @WithUserDetails(value = "양진호@nate.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    public void find_asset_list_with_user_id_and_pagination_by_sub_category_test() throws Exception {
        // given
        String categoryName = "luxury";
        String subCategoryName = "man";
        String page = "0";
        String size = "28";

        // when
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                .get("/assets/{categoryName}/{subCategoryName}", categoryName, subCategoryName)
                .param("page", page)
                .param("size", size));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));
    }

    @DisplayName("하위 카테고리별 에셋 조회 비로그인 성공")
    @Test
    public void find_asset_list_with_pagination_by_sub_category_test() throws Exception {
        // given
        String categoryName = "A";
        String subCategoryName = "AA";
        String page = "0";
        String size = "4";

        // when
        ResultActions resultActions = mockMvc.perform(RestDocumentationRequestBuilders
                .get("/assets/{categoryName}/{subCategoryName}", categoryName, subCategoryName)
                .param("page", page)
                .param("size", size));
        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        System.out.println("테스트 : " + responseBody);

        // Then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.status").value(200));
    }
}
