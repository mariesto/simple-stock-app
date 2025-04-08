package com.mariesto.simplestockapp.controller;

import com.mariesto.simplestockapp.model.TradeRequest;
import com.mariesto.simplestockapp.model.UserStockResponse;
import com.mariesto.simplestockapp.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping("/{userId}")
    public ResponseEntity<Object> fetchAllStocks(@PathVariable String userId) {
        List<UserStockResponse> userStockResponses = stockService.fetchUserStocks(userId);
        return ResponseEntity.ok(userStockResponses);
    }

    @GetMapping("/{userId}/trades")
    public ResponseEntity<Object> fetchAllTrades(@PathVariable String userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(stockService.fetchUserTrades(userId, page, size));
    }

    @PostMapping("/trade")
    public ResponseEntity<Object> executeTrade(@RequestBody TradeRequest request) {
        stockService.executeTrade(request);
        return ResponseEntity.ok().build();
    }
}
