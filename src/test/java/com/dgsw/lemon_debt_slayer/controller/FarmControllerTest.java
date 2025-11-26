package com.dgsw.lemon_debt_slayer.controller;

import com.dgsw.lemon_debt_slayer.dto.farm.TreePurchaseRequest;
import com.dgsw.lemon_debt_slayer.dto.farm.TreeResponse;
import com.dgsw.lemon_debt_slayer.dto.farm.HarvestRequest;
import com.dgsw.lemon_debt_slayer.dto.farm.HarvestResponse;
import com.dgsw.lemon_debt_slayer.dto.farm.FarmViewResponse;
import com.dgsw.lemon_debt_slayer.dto.farm.TreeRemoveRequest;
import com.dgsw.lemon_debt_slayer.service.FarmService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FarmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FarmService farmService;

    private TreePurchaseRequest purchaseRequest;
    private TreeResponse treeResponse;
    private HarvestRequest harvestRequest;
    private HarvestResponse harvestResponse;
    private FarmViewResponse farmViewResponse;

    @BeforeEach
    void setUp() {
        purchaseRequest = TreePurchaseRequest.builder()
                .x(5)
                .y(3)
                .playerId(1L)
                .build();

        treeResponse = TreeResponse.builder()
                .id(1L)
                .x(5)
                .y(3)
                .lastHarvestTime(LocalDateTime.now())
                .productionRate(60)
                .currentLemons(0)
                .build();

        harvestRequest = HarvestRequest.builder()
                .x(5)
                .y(3)
                .playerId(1L)
                .build();

        harvestResponse = HarvestResponse.builder()
                .harvestedLemons(3)
                .message("3개의 레몬을 수확했습니다!")
                .build();

        farmViewResponse = FarmViewResponse.builder()
                .farmMap("=== 농장 현황 ===")
                .totalTrees(1)
                .totalLemons(3)
                .build();
    }

    @Test
    @DisplayName("POST /api/farms/trees - 나무 구매 성공")
    void purchaseTree_Success() throws Exception {
        when(farmService.purchaseTree(any(TreePurchaseRequest.class)))
                .thenReturn(treeResponse);

        mockMvc.perform(post("/api/farms/trees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(purchaseRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.x").value(5))
                .andExpect(jsonPath("$.y").value(3))
                .andExpect(jsonPath("$.currentLemons").value(0));

        verify(farmService, times(1)).purchaseTree(any(TreePurchaseRequest.class));
    }

    @Test
    @DisplayName("POST /api/farms/trees - 유효하지 않은 좌표")
    void purchaseTree_InvalidCoordinates() throws Exception {
        when(farmService.purchaseTree(any(TreePurchaseRequest.class)))
                .thenThrow(new IllegalArgumentException("유효하지 않은 좌표입니다."));

        mockMvc.perform(post("/api/farms/trees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(purchaseRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("유효하지 않은 좌표입니다."));
    }

    @Test
    @DisplayName("PUT /api/farms/harvest - 레몬 수확 성공")
    void harvestLemons_Success() throws Exception {
        when(farmService.harvestLemons(any(HarvestRequest.class)))
                .thenReturn(harvestResponse);

        mockMvc.perform(put("/api/farms/harvest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(harvestRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.harvestedLemons").value(3))
                .andExpect(jsonPath("$.message").value("3개의 레몬을 수확했습니다!"));

        verify(farmService, times(1)).harvestLemons(any(HarvestRequest.class));
    }

    @Test
    @DisplayName("PUT /api/farms/harvest - 나무 없음")
    void harvestLemons_TreeNotFound() throws Exception {
        when(farmService.harvestLemons(any(HarvestRequest.class)))
                .thenThrow(new IllegalArgumentException("해당 위치에 나무가 없습니다."));

        mockMvc.perform(put("/api/farms/harvest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(harvestRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("해당 위치에 나무가 없습니다."));
    }

    @Test
    @DisplayName("GET /api/farms - 농장 조회 성공")
    void viewFarm_Success() throws Exception {
        when(farmService.viewFarm(1L)).thenReturn(farmViewResponse);

        mockMvc.perform(get("/api/farms")
                        .param("playerId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalTrees").value(1))
                .andExpect(jsonPath("$.totalLemons").value(3))
                .andExpect(jsonPath("$.farmMap").exists());

        verify(farmService, times(1)).viewFarm(1L);
    }

    @Test
    @DisplayName("DELETE /api/farms/trees/{x}/{y} - 나무 제거 성공")
    void removeTree_Success() throws Exception {
        TreeRemoveRequest removeRequest = TreeRemoveRequest.builder()
                .playerId(1L)
                .build();

        doNothing().when(farmService).removeTree(eq(5), eq(3), eq(1L));

        mockMvc.perform(delete("/api/farms/trees/5/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(removeRequest)))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(farmService, times(1)).removeTree(5, 3, 1L);
    }

    @Test
    @DisplayName("DELETE /api/farms/trees/{x}/{y} - 나무 없음")
    void removeTree_TreeNotFound() throws Exception {
        TreeRemoveRequest removeRequest = TreeRemoveRequest.builder()
                .playerId(1L)
                .build();

        doThrow(new IllegalArgumentException("해당 위치에 나무가 없습니다."))
                .when(farmService).removeTree(eq(5), eq(3), eq(1L));

        mockMvc.perform(delete("/api/farms/trees/5/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(removeRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("해당 위치에 나무가 없습니다."));
    }
}