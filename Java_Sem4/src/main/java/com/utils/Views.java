package com.utils;

public final class Views {
	//1.UNIT
	public static String TBL_UNIT = "Unit";
	public static String COL_UNIT_ID = "Id";
	public static String COL_UNIT_NAME = "Name";
	
	//2.Conversion
	public static String TBL_CONVERSION = "Conversion";
	public static String COL_CONVERSION_ID = "Id";
	public static String COL_CONVERSION_PRODUCT_ID ="Product_id";
	public static String COL_CONVERSION_FROM_UNIT_ID = "From_unit_id";
	public static String COL_CONVERSION_TO_UNIT_ID ="To_unit_id";
	public static String COL_CONVERSION_RATE = "Conversion_rate";
	
	//3.BRAND
	public static String BRAND_SHOWBRAND = "admin/brand/showBrand";
	public static String BRAND_SHOWADDBRAND = "admin/brand/showAddBrand";
	public static String BRAND_SHOWUPDATEBRAND = "admin/brand/showUpdateBrand";
	
	public static String TBL_BRAND = "Brand";
	public static String COL_BRAND_ID = "Id";
	public static String COL_BRAND_NAME ="Name";
	
	//4.PRODUCT
	public static String PRODUCT_SHOWPRODUCT = "admin/product/showProduct";
	public static String PRODUCT_SHOWADDPRODUCT ="admin/product/showAddProduct";
	public static String PRODUCT_SHOWUPDATEPRODUCT = "admin/product/showUpdateProduct";
	public static String PRODUCT_SHOWPRODUCTDETAIL ="admin/product/showProductDetail";
	
	public static String TBL_PRODUCT = "Product";
	public static String COL_PRODUCT_ID ="Id";
	public static String COL_PRODUCT_NAME = "Product_name";
	public static String COL_PRODUCT_CATE_ID = "Cate_Id";
	public static String COL_PRODUCT_BRAND_ID = "Brand_Id";
	public static String COL_PRODUCT_UNIT_ID = "Id_Conversion";
	public static String COL_PRODUCT_PRICE = "Price";
	public static String COL_PRODUCT_IMG = "Img";
	public static String COL_PRODUCT_DESCIPTION ="Description";
	public static String COL_PRODUCT_WARRANTY_PERIOD ="Warranty_period";
	
	//5.PRODUCT_CATEGORY
	public static String CATEGORY_SHOWCATEGORY = "admin/category/showCategory";
	public static String CATEGORY_SHOWADDCATEGORY = "admin/category/showAddCategory";
	public static String CATEGORY_SHOWUPDATECATEGORY = "admin/category/showUpdateCategory";
	
	public static String TBL_CATEGORY = "Product_category";
	public static String COL_CATEGORY_ID ="Id";
	public static String COL_CATEGORY_NAME = "Cate_name";
	
	//6.PRODUCT_PRICE_CHANGE
	public static String TBL_PRODUCT_PRICE_CHANGE = "Product_price_change";
	public static String COL_PRICE_CHANGE_ID = "Id";
	public static String COL_PRICE_CHANGE_PRODUCT_ID = "Product_Id";
	public static String COL_PRICE_CHANGE_PRICE = "Price";
	public static String COL_PRICE_CHANGE_DATE_START = "Date_start";
	public static String COL_PRICE_CHANGE_DATE_END = "Date_end";
	
	//7.STOCK
	public static String TBL_STOCK = "Stock";
	public static String COL_STOCK_ID = "Id";
	public static String COL_STOCK_PRODUCT_ID = "Id_product";
	public static String COL_STOCK_QUANTITY = "Quantity";
	public static String COL_STOCK_WARERCDT_ID = "Wh_rc_dt_id";
	
	//8.WAREHOUSE_RECEIPT_DETAIL
	public static String TBL_WAREHOUSE_RECEIPT_DETAIL = "Warehouse_receipt_detail";
	public static String COL_WAREHOUSE_RECEIPT_DETAIL_ID = "Id";
	public static String COL_DETAIL_WAREHOUSE_RECEIPT_ID = "Wh_receiptId";
	public static String COL_WAREHOUSE_RECEIPT_DETAIL_PRODUCT_ID = "Id_product";
	public static String COL_WAREHOUSE_RECEIPT_DETAIL_QUANTITY = "Quantity";
	public static String COL_WAREHOUSE_RECEIPT_DETAIL_WH_PRICE = "Wh_price";
	
	//9.WAREHOUSE_RECEIPT 
	public static String ADD_WAREHOUSE_RECEIPT = "warehouseManager/warehouseReceipt/showAddWhAndWhDetails";
	public static String SHOW_WAREHOUSE_RECEIPT = "warehouseManager/warehouseReceipt/showWhReceipt";
	public static String UPDATE_WAREHOUSE_RECEIPT = "warehouseManager/warehouseReceipt/showUpdateWhAndWhDetails";
	
	public static String TBL_WAREHOUSE_RECEIPT = "Warehouse_receipt";
	public static String COL_WAREHOUSE_RECEIPT_ID = "Id";
	public static String COL_WAREHOUSE_RECEIPT_NAME = "Name";
	public static String COL_WAREHOUSE_RECEIPT_IDWH = "Wh_Id";
	public static String COL_WAREHOUSE_RECEIPT_DATE = "Date";
	
	//10.WAREHOUSE
	public static String WAREHOUSE_SHOWWAREHOUSE = "admin/warehouse/showWarehouse";
	public static String WAREHOUSE_SHOWADDWAREHOUSE = "admin/warehouse/showAddWarehouse";
	public static String WAREHOUSE_SHOWUPDATEWAREHOUSE = "admin/warehouse/showUpdateWarehouse";
	
	
	public static String TBL_WAREHOUSE = "Warehouse";
	public static String COL_WAREHOUSE_ID = "Id";
	public static String COL_WAREHOUSE_NAME = "Name";
	public static String COL_WAREHOUSE_ADDRESS = "Address";
	public static String COL_WAREHOUSE_TYPE_WAREHOUSE_ID = "Wh_type_Id";
	
	//11.WAREHOUSE_TYPE
	public static String WAREHOUSETYPE_SHOWWHTYPE="admin/warehouseType/showWhType";
	public static String WAREHOUSETYPE_SHOWADDWHTYPE = "admin/warehouseType/showAddWhType";
	public static String WAREHOUSETYPE_SHOWUPDATEWHTYPE ="admin/warehouseType/showUpdateWhType";
	
	public static String TBL_WAREHOUSE_TYPE = "Warehouse_type";
	public static String COL_WAREHOUSE_TYPE_ID = "Id";
	public static String COL_WAREHOUSE_TYPE_NAME = "Name";
	
	//12.Warehouse_rn_detail
	public static String TBL_WAREHOUSE_RN_DETAIL = "Warehouse_rn_detail";
	public static String COL_WAREHOUSE_RN_DETAIL_ID = "Id";
	public static String COL_WAREHOUSE_RNOTE_ID = "Wgrn_id";
	public static String COL_WAREHOUSE_RN_DETAIL_STATUS = "Status";
	public static String COL_WAREHOUSE_RN_DETAIL_STOCK_ID = "Stock_Id";
	public static String COL_WAREHOUSE_RN_DETAIL_QUANTITY = "Quantity";
	
	//13.Warehouse_releasenote
	public static String TBL_WAREHOUSE_RELEASENOTE = "Warehouse_releasenote";
	public static String COL_WAREHOUSE_RELEASENOTE_ID = "Id";
	public static String COL_WAREHOUSE_RELEASENOTE_NAME = "Name";
	public static String COL_WAREHOUSE_RELEASENOTE_DATE = "Date";
	public static String COL_WAREHOUSE_RELEASENOTE_STATUS = "Status";
	public static String COL_WAREHOUSE_RELEASENOTE_ORDER_ID = "Order_id";
	
	//14.PAYMENT 
	public static String TBL_PAYMENT = "Payment";
	public static String COL_PAYMENT_ID = "Id";
	public static String COL_PAYMENT_NAME = "Name";
	
	//15.CUSTOMER
	public static String TBL_CUSTOMER = "Customer";
	public static String COL_CUSTOMER_ID = "Id";
	public static String COL_CUSTOMER_FIRST_NAME = "First_name";
	public static String COL_CUSTOMER_LAST_NAME = "Last_name";
	public static String COL_CUSTOMER_ADDRESS = "Address";
	public static String COL_CUSTOMER_PASSWORD = "Password";
	public static String COL_CUSTOMER_EMAIL = "Email";
	public static String COL_CUSTOMER_CREATION_TIME = "Creation_time";
	public static String COL_CUSTOMER_BIRTHOFDATE = "Dob";
	
	//16.SHOPING CART
	public static String TBL_SHOPING_CART = "shopping_cart";
	public static String COL_SHOPING_CART_ID = "Id";
	public static String COL_SHOPING_CART_STATUS = "Status";
	public static String COL_SHOPING_CART_CUSTOMER_ID = "Customer_Id";
	public static String COL_SHOPING_CART_PRODUCT_ID = "Product_id";
	public static String COL_SHOPING_CART_QUANTITY = "Quantity";
	
	//17.ORDER
	public static String TBL_ORDER = "Order";
	public static String COL_ORDER_ID = "Id";
	public static String COL_ORDER_CUSTOMER_ID = "Customer_Id";
	public static String COL_ORDER_STATUS = "Status";
	public static String COL_ORDER_EMPLOYEE = "Employee_Id";
	public static String COL_ORDER_PAYMENT_ID ="Payment_Id";
	
	//18.ORDER DETAIL
	public static String TBL_ORDER_DETAIL = "Order_detail";
	public static String COL_ORDER_DETAIL_ID = "Id";
	public static String COL_ORDER_DETAIL_STOCK_ID = "Stock_id";
	public static String COL_ORDER_DETAIL_ORDER_ID = "order_id";
	public static String COL_ORDER_DETAIL_PRICE = "Status";
	public static String COL_ORDER_DETAIL_STATUS = "Status";
	
	//19.EMPLOYEE
	public static String EMPLOYEE_SHOWEMP = "admin/employee/showEmp";
	public static String EMPLOYEE_SHOWREGISTER = "admin/employee/showRegister";
	public static String EMPLOYEE_SHOWEMPLOYEEDETAIL = "admin/employee/showEmployeeDetail";
	public static String EMPLOYEE_LOGIN = "login";
	public static String EMPLOYEE_SHOWUPDATEMPLOYEE = "admin/employee/showUpdateEmployee";
	
	public static String TBL_EMPLOYEE = "Employee";
	public static String COL_EMPLOYEE_ID = "Id";
	public static String COL_EMPLOYEE_FIRST_NAME = "First_name";
	public static String COL_EMPLOYEE_LAST_NAME = "Last_name";
	public static String COL_EMPLOYEE_ROLE_ID = "Role_Id";
	public static String COL_EMPLOYEE_PASSWORD = "Password";
	public static String COL_EMPLOYEE_PHONE = "Phone";
	//20.ROLE 
	public static String TBL_ROLE = "Role";
	public static String COL_ROLE_ID = "Id";
	public static String COL_ROLE_NAME = "Name";
	//21.CUSTOMER
	public static String CUS_SHOWPAGEMAIN = "customer/pagemain";
	public static String CUS_SHOPPINGPAGE = "customer/shoppingpage";
	public static String CUS_DETAILPROPAGE = "customer/detailproduct";
	public static String CUS_CARTPAGE = "customer/cart";
	public static String CUS_SIGNINPAGE = "customer/signin";
	public static String CUS_SIGNUPPAGE = "customer/signup";
	public static String CUS_COFIRMPAGE = "customer/corfirmregister";
}
