package com.customer.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.models.*;
import com.models.ghn.District;
import com.models.ghn.Province;
import com.models.ghn.StoreRequest;
import com.models.ghn.StoreResponse;
import com.models.ghn.Ward;
@Repository
public class GHNService {
	private static final String TOKEN = "858b9bf9-ab01-11ef-81b6-5e2e958f07fa";
	
	public List<Province> getProvinces() {
	    try {
	        String apiUrl = "https://online-gateway.ghn.vn/shiip/public-api/master-data/province";
	        
	        HttpHeaders headers = new HttpHeaders();
	        headers.set("Token", TOKEN);
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        
	        HttpEntity<String> entity = new HttpEntity<>(headers);
	        
	        RestTemplate restTemplate = new RestTemplate();
	        
	        // Thêm log để xem response JSON thực tế
	        ResponseEntity<String> rawResponse = restTemplate.exchange(
	            apiUrl,
	            HttpMethod.GET,
	            entity,
	            String.class
	        );
	        System.out.println("Raw Response: " + rawResponse.getBody());
	        
	        // Gọi API với model Province
	        ResponseEntity<GHNResponse<List<Province>>> response = restTemplate.exchange(
	            apiUrl,
	            HttpMethod.GET,
	            entity,
	            new ParameterizedTypeReference<GHNResponse<List<Province>>>() {}
	        );
	        
	        System.out.println("Response Code: " + response.getStatusCode());
	        if (response.getBody() != null) {
	            System.out.println("GHN Code: " + response.getBody().getCode());
	            System.out.println("GHN Message: " + response.getBody().getMessage());
	        }
	        
	        if (response.getBody() != null && response.getBody().getData() != null) {
	            List<Province> provinces = response.getBody().getData();
	            return provinces;
	        }
	        
	        return new ArrayList<>();
	        
	    } catch (Exception e) {
	        System.out.println("Error: " + e.getMessage());
	        e.printStackTrace();
	        return new ArrayList<>();
	    }
	}
	
	public List<District> getDistricts(Integer provinceId) {
        try {
            String apiUrl = "https://online-gateway.ghn.vn/shiip/public-api/master-data/district";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Token", TOKEN);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Tạo request body
            HttpEntity<String> entity = new HttpEntity<>("{\"province_id\":" + provinceId + "}", headers);
            
            RestTemplate restTemplate = new RestTemplate();
            
            ResponseEntity<GHNResponse<List<District>>> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,  // GHN dùng POST cho API này
                entity,
                new ParameterizedTypeReference<GHNResponse<List<District>>>() {}
            );
            if (response.getBody() != null && response.getBody().getData() != null) {
	            List<District> district = response.getBody().getData();
	            return district;
	        }
            if (response.getBody() != null && response.getBody().getData() != null) {
                return response.getBody().getData();
            }
            
            return new ArrayList<>();
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Ward> getWards(Integer districtId) {
        try {
            String apiUrl = "https://online-gateway.ghn.vn/shiip/public-api/master-data/ward";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Token", TOKEN);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Tạo request body
            HttpEntity<String> entity = new HttpEntity<>("{\"district_id\":" + districtId + "}", headers);
            
            RestTemplate restTemplate = new RestTemplate();
            
            ResponseEntity<GHNResponse<List<Ward>>> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,  // GHN dùng POST cho API này
                entity,
                new ParameterizedTypeReference<GHNResponse<List<Ward>>>() {}
            );
            if (response.getBody() != null && response.getBody().getData() != null) {
	            List<Ward> ward = response.getBody().getData();
	            return ward;
	        }
            if (response.getBody() != null && response.getBody().getData() != null) {
                return response.getBody().getData();
            }
            
            return new ArrayList<>();
            
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
	
    public StoreResponse createStore(StoreRequest request) {
        try {
            String apiUrl = "https://online-gateway.ghn.vn/shiip/public-api/v2/shop/register";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Token", TOKEN);
            headers.set("shop_id", String.valueOf(request.getShop_id()));
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<StoreRequest> entity = new HttpEntity<>(request, headers);
            RestTemplate restTemplate = new RestTemplate();
            
            // Log raw response để xem format JSON thực tế
            ResponseEntity<String> rawResponse = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                String.class
            );
            System.out.println("Raw Response JSON: " + rawResponse.getBody());
            
            
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(rawResponse.getBody());
            System.out.println("Response code: " + rootNode.get("code").asInt());
            System.out.println("Response message: " + rootNode.get("message").asText());
            if (rootNode.has("data")) {
                JsonNode dataNode = rootNode.get("data");
                System.out.println("Store Data: " + dataNode.toString());
            }
            
            ResponseEntity<GHNResponse<StoreResponse>> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<GHNResponse<StoreResponse>>() {}
            );
            
            if (response.getBody() != null && response.getBody().getData() != null) {
                StoreResponse storeResponse = response.getBody().getData();
                // Log để kiểm tra
                System.out.println("Parsed Store ID: " + storeResponse.getShopId());
                System.out.println("Parsed Name: " + storeResponse.getName());
                return storeResponse;
            }
            
            return null;
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    public void testCreateStore() {
        // Tạo request object
        StoreRequest request = new StoreRequest();
        request.setName("Kho Test GHN1");
        request.setPhone("0987654321");
        request.setAddress("123 Đường Test");
        request.setWard_code("420302");
        request.setDistrict_id(1837);
        request.setShop_id(32);
        
        
        StoreResponse response = createStore(request);
        
        // In thông tin kho
        if (response != null) {
            System.out.println("=== THÔNG TIN SHOP VỪA TẠO ===");
            System.out.println("Shop ID: " + response.getShopId());
        } else {
            System.out.println("Tạo shop thất bại!");
        }
    }
    public ShippingFeeResponse calculateFee(int fromDistrictId, int toDistrictId, Product product) {
        try {
            String apiUrl = "https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee";
            
            HttpHeaders headers = new HttpHeaders();
            headers.set("Token", TOKEN);
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("from_district_id", fromDistrictId);
            requestBody.put("to_district_id", toDistrictId);
            requestBody.put("service_type_id", 2);
            
          
            requestBody.put("weight", 3000);
            requestBody.put("length", 100);
            requestBody.put("width", 100);
            requestBody.put("height", 100);
            requestBody.put("insurance_value", 100000);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<GHNResponse<ShippingFeeResponse>> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<GHNResponse<ShippingFeeResponse>>() {}
            );
            
            if (response.getBody() != null) {
                return response.getBody().getData();
            }
            
            return null;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public double calculateShippingFee(int fromDistrictId, int toDistrictId, Product product) {
   
        
        ShippingFeeResponse fee = calculateFee(fromDistrictId, toDistrictId, product);
        
        if (fee != null) {
            return fee.getTotal();
        }
        
        return 0;
    }
}

class ShippingFeeResponse {
    private int total;              // Tổng phí
    private int service_fee;        // Phí dịch vụ
    private int insurance_fee;      // Phí bảo hiểm
    private int pick_station_fee;   // Phí điểm lấy hàng
    private int coupon_value;       // Giá trị coupon
    private int r2s_fee;           // Phí return to sender
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public int getService_fee() {
		return service_fee;
	}
	public void setService_fee(int service_fee) {
		this.service_fee = service_fee;
	}
	public int getInsurance_fee() {
		return insurance_fee;
	}
	public void setInsurance_fee(int insurance_fee) {
		this.insurance_fee = insurance_fee;
	}
	public int getPick_station_fee() {
		return pick_station_fee;
	}
	public void setPick_station_fee(int pick_station_fee) {
		this.pick_station_fee = pick_station_fee;
	}
	public int getCoupon_value() {
		return coupon_value;
	}
	public void setCoupon_value(int coupon_value) {
		this.coupon_value = coupon_value;
	}
	public int getR2s_fee() {
		return r2s_fee;
	}
	public void setR2s_fee(int r2s_fee) {
		this.r2s_fee = r2s_fee;
	}
    
   
}
class GHNResponse<T> {
    private int code;
    private String message;
    private T data;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
    
    
}







