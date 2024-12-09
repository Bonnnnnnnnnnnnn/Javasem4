package com.admin.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.admin.repository.StatisticsRepository;
import com.customer.repository.ShoppingpageRepository;
import com.models.PageView;
import com.utils.Views;


@Controller
@RequestMapping("/admin/statistics")
public class StatisticsController {
    
    @Autowired
    StatisticsRepository repstat;
    
    @Autowired
    ShoppingpageRepository repspp;


    @GetMapping("/showpage")
    public String showpage(Model model) {
    	int[] idCategories = {};
		int[] idBrands = {};
		String[] statuses = { "NewRelease", "Active", "OutOfStock", "Inactive" };
        model.addAttribute("products", repspp.findAllnopaging(new PageView(), "", idCategories, idBrands, statuses));
    
        return Views.ADMIN_STATISTICSPAGE;
    }

    @GetMapping("/api/revenue/{period}")
    @ResponseBody
    public Map<String, Object> getRevenue(
        @PathVariable String period,
        @RequestParam(required = false) Long productId) {
        
        List<Map<String, Object>> data = repstat.getRevenue(period, productId);
        
        List<String> labels = new ArrayList<>();
        List<Double> values = new ArrayList<>();
        List<Double> totalPeriodRevenue = new ArrayList<>();
        List<Double> percentage = new ArrayList<>();

        for (Map<String, Object> row : data) {
            labels.add(row.get("timePeriod").toString());
            values.add(((Number)row.get("SelectedRevenue")).doubleValue());
            totalPeriodRevenue.add(((Number)row.get("TotalRevenue")).doubleValue());
            percentage.add(((Number)row.get("SelectedPercentage")).doubleValue());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("values", values);
        result.put("totalPeriodRevenue", totalPeriodRevenue);
        result.put("percentage", percentage);

        return result;
    }

  
    @GetMapping("/products/{period}")
    public ResponseEntity<Map<String, Object>> getProductStats(
            @PathVariable String period,
            @RequestParam(required = false) Integer productId) {
        try {
            Map<String, Object> response = new HashMap<>();
            
            // Lấy summary trend (dữ liệu cho chart)
            List<Map<String, Object>> summaryTrend = repstat.getProductSummaryTrend(period);
            
            // Lấy best selling products
            List<Map<String, Object>> bestSellers = repstat.getBestSellingProducts(period);
            
            // Lấy summary hiện tại (từ best sellers)
            Map<String, Object> latestSummary = new HashMap<>();
            if (!bestSellers.isEmpty()) {
                // Lấy dữ liệu từ sản phẩm đứng đầu của kỳ gần nhất
                Map<String, Object> topProduct = bestSellers.stream()
                    .filter(p -> ((Number)p.get("rank")).intValue() == 1)
                    .findFirst()
                    .orElse(new HashMap<>());
                    
                latestSummary.put("bestProduct", topProduct.get("productName"));
                latestSummary.put("bestProductRevenue", topProduct.get("revenue"));
                latestSummary.put("totalUnitsSold", topProduct.get("totalQuantity"));
                
                // Tính average price
                if (topProduct.get("totalQuantity") != null && topProduct.get("revenue") != null) {
                    double revenue = ((Number)topProduct.get("revenue")).doubleValue();
                    long quantity = ((Number)topProduct.get("totalQuantity")).longValue();
                    latestSummary.put("averagePrice", quantity > 0 ? revenue/quantity : 0);
                } else {
                    latestSummary.put("averagePrice", 0);
                }

                // Tính return rate
                if (topProduct.get("returnQuantity") != null && topProduct.get("totalQuantity") != null) {
                    long returnQty = ((Number)topProduct.get("returnQuantity")).longValue();
                    long totalQty = ((Number)topProduct.get("totalQuantity")).longValue();
                    double returnRate = totalQty > 0 ? (returnQty * 100.0 / totalQty) : 0;
                    latestSummary.put("returnRate", Math.round(returnRate * 100.0) / 100.0); // Round to 2 decimal places
                } else {
                    latestSummary.put("returnRate", 0.0);
                }
            }
            
            // Lấy top products
            List<Map<String, Object>> topProducts = repstat.getTopProducts(period);
            
            response.put("summary", latestSummary);
            response.put("summaryTrend", summaryTrend);
            response.put("topProducts", topProducts);
            response.put("bestSellers", bestSellers);
            response.put("period", period);
            response.put("totalPeriods", summaryTrend.size());
                     
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Failed to fetch product statistics",
                        "message", e.getMessage()
                    ));
        }
    }
    @GetMapping("/api/warehouse-revenue/{period}")
    @ResponseBody
    public Map<String, Object> getWarehouseRevenue(@PathVariable String period) {
        List<Map<String, Object>> rawData = repstat.getWarehouseRevenue(period);
        
        // Get unique warehouses and time periods
        Set<String> warehouses = new TreeSet<>();
        Set<String> timePeriods = new TreeSet<>();
        
        for (Map<String, Object> row : rawData) {
            warehouses.add(row.get("warehouseName").toString());
            timePeriods.add(row.get("timePeriod").toString());
        }

        // Calculate total revenue for each period
        Map<String, Double> periodTotals = new HashMap<>();
        for (Map<String, Object> row : rawData) {
            String periodd = row.get("timePeriod").toString();
            Double revenue = ((Number)row.get("NetRevenue")).doubleValue();
            periodTotals.merge(periodd, revenue, Double::sum);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", rawData);  // Original data for table
        result.put("warehouses", new ArrayList<>(warehouses));  // List of warehouses
        result.put("labels", new ArrayList<>(timePeriods));  // Time periods
        result.put("periodTotals", periodTotals);  // Total revenue per period

        return result;
    }
    
    @GetMapping("/province-revenue/{period}")
    public ResponseEntity<Map<String, Object>> getProvinceRevenue(@PathVariable String period) {
        try {
            // Validate period
            if (!Arrays.asList("monthly", "quarterly", "yearly").contains(period)) {
                throw new IllegalArgumentException("Invalid period: " + period);
            }
            
            Map<String, Object> response = repstat.getProvinceRevenue(period);
            
            // Kiểm tra null và empty
            if (response == null || response.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            // Log error
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @GetMapping("/gross-profit/{period}")
    public ResponseEntity<?> getGrossProfitStats(@PathVariable String period) {
        List<Map<String, Object>> rawData = repstat.getGrossProfitAnalysis(period);
        
        // Tạo response structure
        Map<String, Object> response = new HashMap<>();
        
        // 1. Monthly Summary cho line/column chart
        List<Map<String, Object>> monthlySummary = new ArrayList<>();
        Map<String, Map<String, Object>> summaryByMonth = new HashMap<>();
        
        // Group by month
        for (Map<String, Object> row : rawData) {
            String month = row.get("timePeriod").toString();
            summaryByMonth.computeIfAbsent(month, k -> new HashMap<>());
            
            Map<String, Object> summary = summaryByMonth.get(month);
            summary.merge("units", ((Number)row.get("quantitySold")).intValue(), (a, b) -> 
                ((Number)a).intValue() + ((Number)b).intValue());
            summary.merge("revenue", ((Number)row.get("grossRevenue")).doubleValue(), (a, b) -> 
                ((Number)a).doubleValue() + ((Number)b).doubleValue());
            summary.merge("cost", ((Number)row.get("totalCost")).doubleValue(), (a, b) -> 
                ((Number)a).doubleValue() + ((Number)b).doubleValue());
            summary.merge("profit", ((Number)row.get("grossProfit")).doubleValue(), (a, b) -> 
                ((Number)a).doubleValue() + ((Number)b).doubleValue());
        }
        
        // Convert to list và tính margin
        for (Map.Entry<String, Map<String, Object>> entry : summaryByMonth.entrySet()) {
            Map<String, Object> monthData = new HashMap<>();
            monthData.put("period", entry.getKey());
            monthData.put("units", entry.getValue().get("units"));
            monthData.put("revenue", entry.getValue().get("revenue"));
            monthData.put("cost", entry.getValue().get("cost"));
            monthData.put("profit", entry.getValue().get("profit"));
            
            double revenue = ((Number)entry.getValue().get("revenue")).doubleValue();
            double profit = ((Number)entry.getValue().get("profit")).doubleValue();
            double margin = revenue > 0 ? (profit * 100.0 / revenue) : 0;
            monthData.put("margin", margin);
            
            monthlySummary.add(monthData);
        }
        
        // 2. Product Performance cho stacked bar chart
        List<Map<String, Object>> productPerformance = new ArrayList<>(rawData);
        
        // Add vào response
        response.put("monthlySummary", monthlySummary);
        response.put("productPerformance", productPerformance);
        
        return ResponseEntity.ok(response);
    }
    
    
}