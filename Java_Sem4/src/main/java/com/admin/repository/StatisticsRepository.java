package com.admin.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.utils.Views;

@Repository
public class StatisticsRepository {

	@Autowired
	JdbcTemplate db;

	public List<Map<String, Object>> getRevenue(String period, Long productId) {
	    String timeFormat = switch (period) {
	        case "monthly" -> "FORMAT(DATEFROMPARTS(YEAR(o.[" + Views.COL_ORDER_DATE + "]), MONTH(o.[" + Views.COL_ORDER_DATE + "]), 1), 'yyyy-MM')";
	        case "quarterly" -> "CAST(YEAR(o.[" + Views.COL_ORDER_DATE + "]) AS VARCHAR) + '-Q' + CAST(DATEPART(QUARTER, o.[" + Views.COL_ORDER_DATE + "]) AS VARCHAR)";
	        case "yearly" -> "CAST(YEAR(o.[" + Views.COL_ORDER_DATE + "]) AS VARCHAR)";
	        default -> throw new IllegalArgumentException("Invalid period: " + period);
	    };

	    String groupBy = switch (period) {
	        case "monthly" -> "YEAR(o.[" + Views.COL_ORDER_DATE + "]), MONTH(o.[" + Views.COL_ORDER_DATE + "])";
	        case "quarterly" -> "YEAR(o.[" + Views.COL_ORDER_DATE + "]), DATEPART(QUARTER, o.[" + Views.COL_ORDER_DATE + "])";
	        case "yearly" -> "YEAR(o.[" + Views.COL_ORDER_DATE + "])";
	        default -> throw new IllegalArgumentException("Invalid period: " + period);
	    };

	    String periodCondition = getPeriodCondition(period);

	    String sql = """
	        WITH AllPeriods AS (
	            SELECT DISTINCT
	                %s as timePeriod
	            FROM [%s] o
	            WHERE o.[%s] = 'Completed'
	                AND %s
	        ),
	        SelectedRevenue AS (
	            SELECT
	                %s as timePeriod,
	                SUM(od.Price * od.Quantity) as GrossRevenue,
	                COALESCE(SUM(CASE 
	                    WHEN ro.[%s] IN ('Accepted', 'Completed') 
	                    THEN rod.Amount 
	                    ELSE 0 
	                END), 0) as ReturnAmount
	            FROM [%s] o
	            JOIN [%s] od ON o.[%s] = od.[%s]
	            LEFT JOIN [%s] rod ON od.Id = rod.Order_Detail_Id
	            LEFT JOIN [%s] ro ON rod.Return_Order_Id = ro.Id
	            WHERE o.[%s] = 'Completed'
	                AND od.[%s] = %s
	                AND %s
	            GROUP BY %s
	        ),
	        TotalRevenue AS (
	            SELECT
	                %s as timePeriod,
	                SUM(od.Price * od.Quantity) as GrossRevenue,
	                COALESCE(SUM(CASE 
	                    WHEN ro.[%s] IN ('Accepted', 'Completed') 
	                    THEN rod.Amount 
	                    ELSE 0 
	                END), 0) as ReturnAmount
	            FROM [%s] o
	            JOIN [%s] od ON o.[%s] = od.[%s]
	            LEFT JOIN [%s] rod ON od.Id = rod.Order_Detail_Id
	            LEFT JOIN [%s] ro ON rod.Return_Order_Id = ro.Id
	            WHERE o.[%s] = 'Completed'
	                AND %s
	            GROUP BY %s
	        )
	        SELECT
	            m.timePeriod,
	            ISNULL(sr.GrossRevenue - sr.ReturnAmount, 0) as SelectedRevenue,
	            ISNULL(tr.GrossRevenue - tr.ReturnAmount, 0) as TotalRevenue,
	            ISNULL(tr.GrossRevenue - tr.ReturnAmount - (sr.GrossRevenue - sr.ReturnAmount), 0) as OtherRevenue,
	            CASE 
	                WHEN ISNULL(tr.GrossRevenue - tr.ReturnAmount, 0) = 0 THEN 0
	                ELSE CAST(ISNULL(sr.GrossRevenue - sr.ReturnAmount, 0) * 100.0 / 
	                    NULLIF(tr.GrossRevenue - tr.ReturnAmount, 0) as DECIMAL(10,2))
	            END as SelectedPercentage,
	            CASE 
	                WHEN ISNULL(tr.GrossRevenue - tr.ReturnAmount, 0) = 0 THEN 0
	                ELSE CAST((ISNULL(tr.GrossRevenue - tr.ReturnAmount, 0) - 
	                    ISNULL(sr.GrossRevenue - sr.ReturnAmount, 0)) * 100.0 / 
	                    NULLIF(tr.GrossRevenue - tr.ReturnAmount, 0) as DECIMAL(10,2))
	            END as OtherPercentage
	        FROM AllPeriods m
	        LEFT JOIN SelectedRevenue sr ON m.timePeriod = sr.timePeriod
	        LEFT JOIN TotalRevenue tr ON m.timePeriod = tr.timePeriod
	        ORDER BY m.timePeriod ASC
	    """.formatted(
	        timeFormat, Views.TBL_ORDER, Views.COL_ORDER_STATUS, periodCondition,
	        timeFormat, Views.COL_RETURN_ORDER_STATUS, Views.TBL_ORDER,
	        Views.TBL_ORDER_DETAIL, Views.COL_ORDER_ID, Views.COL_ORDER_DETAIL_ORDER_ID,
	        Views.TBL_RETURN_ORDER_DETAIL, Views.TBL_RETURN_ORDER,
	        Views.COL_ORDER_STATUS, Views.COL_ORDER_DETAIL_PRODUCT_ID, productId,
	        periodCondition, groupBy,
	        timeFormat, Views.COL_RETURN_ORDER_STATUS, Views.TBL_ORDER,
	        Views.TBL_ORDER_DETAIL, Views.COL_ORDER_ID, Views.COL_ORDER_DETAIL_ORDER_ID,
	        Views.TBL_RETURN_ORDER_DETAIL, Views.TBL_RETURN_ORDER,
	        Views.COL_ORDER_STATUS, periodCondition, groupBy
	    );

	    return db.queryForList(sql);
	}

	public List<Map<String, Object>> getProductSummary(String period) {
		String timeFormat = getTimeFormat(period);
		String timeFormat2 = timeFormat.replace("o.", "o2.");
		String periodCondition = getPeriodCondition(period);
		String periodCondition2 = periodCondition.replace("o.", "o2.");

		String sql = """
				WITH ProductStats AS (
				    SELECT
				        %s as timePeriod,
				        p.%s as productId,
				        p.%s as productName,
				        SUM(o.%s) as grossRevenue,
				        COALESCE(SUM(ro.%s), 0) as returnAmount,
				        SUM(o.%s) - COALESCE(SUM(ro.%s), 0) as netRevenue,
				        SUM(od.%s) as unitsSold,
				        CAST((SUM(o.%s) - COALESCE(SUM(ro.%s), 0)) AS DECIMAL(10,2)) / NULLIF(SUM(od.%s), 0) as avgPrice
				    FROM [%s] p
				    JOIN [%s] od ON p.%s = od.%s
				    JOIN [%s] o ON od.%s = o.%s
				    LEFT JOIN [%s] rod ON od.%s = rod.%s
				    LEFT JOIN [%s] ro ON rod.%s = ro.%s AND ro.%s IN ('Completed', 'Accepted')
				    WHERE o.%s = 'Completed'
				        AND %s
				    GROUP BY %s, p.%s, p.%s
				),
				BestProducts AS (
				    SELECT
				        timePeriod,
				        productName,
				        netRevenue,
				        ROW_NUMBER() OVER(PARTITION BY timePeriod ORDER BY netRevenue DESC) as rn
				    FROM ProductStats
				),
				ReturnRates AS (
				    SELECT
				        %s as timePeriod,
				        ISNULL(
				            CAST(
				                COUNT(DISTINCT CASE
				                    WHEN ro.%s IN ('Completed', 'Accepted')
				                    THEN rod.%s
				                END) * 100.0 /
				                NULLIF(COUNT(DISTINCT od2.%s), 0)
				            AS DECIMAL(10,2))
				        , 0) as rate
				    FROM [%s] od2
				    JOIN [%s] o2 ON od2.%s = o2.%s
				    LEFT JOIN [%s] rod ON od2.%s = rod.%s
				    LEFT JOIN [%s] ro ON rod.%s = ro.%s
				    WHERE o2.%s = 'Completed'
				        AND %s
				    GROUP BY %s
				)
				SELECT
				    ps.timePeriod,
				    bp.productName as bestProduct,
				    bp.netRevenue as bestProductRevenue,
				    SUM(ps.unitsSold) as totalUnitsSold,
				    AVG(ps.avgPrice) as averagePrice,
				    rr.rate as returnRate
				FROM ProductStats ps
				JOIN BestProducts bp ON ps.timePeriod = bp.timePeriod AND bp.rn = 1
				JOIN ReturnRates rr ON ps.timePeriod = rr.timePeriod
				GROUP BY ps.timePeriod, bp.productName, bp.netRevenue, rr.rate
				ORDER BY ps.timePeriod DESC
				""".formatted(timeFormat, Views.COL_PRODUCT_ID, Views.COL_PRODUCT_NAME, Views.COL_ORDER_TOTALAMOUNT,
				Views.COL_RETURN_ORDER_FINAL_AMOUNT, Views.COL_ORDER_TOTALAMOUNT, Views.COL_RETURN_ORDER_FINAL_AMOUNT,
				Views.COL_ORDER_DETAIL_QUANTITY, Views.COL_ORDER_TOTALAMOUNT, Views.COL_RETURN_ORDER_FINAL_AMOUNT,
				Views.COL_ORDER_DETAIL_QUANTITY, Views.TBL_PRODUCT, Views.TBL_ORDER_DETAIL, Views.COL_PRODUCT_ID,
				Views.COL_ORDER_DETAIL_PRODUCT_ID, Views.TBL_ORDER, Views.COL_ORDER_DETAIL_ORDER_ID, Views.COL_ORDER_ID,
				Views.TBL_RETURN_ORDER_DETAIL, Views.COL_ORDER_DETAIL_ID, Views.COL_RETURN_DETAIL_ORDER_DETAIL_ID,
				Views.TBL_RETURN_ORDER, Views.COL_RETURN_DETAIL_RETURN_ID, Views.COL_RETURN_ORDER_ID,
				Views.COL_RETURN_ORDER_STATUS, Views.COL_ORDER_STATUS, periodCondition, timeFormat,
				Views.COL_PRODUCT_ID, Views.COL_PRODUCT_NAME, timeFormat2, Views.COL_RETURN_ORDER_STATUS,
				Views.COL_RETURN_DETAIL_ID, Views.COL_ORDER_DETAIL_ID, Views.TBL_ORDER_DETAIL, Views.TBL_ORDER,
				Views.COL_ORDER_DETAIL_ORDER_ID, Views.COL_ORDER_ID, Views.TBL_RETURN_ORDER_DETAIL,
				Views.COL_ORDER_DETAIL_ID, Views.COL_RETURN_DETAIL_ORDER_DETAIL_ID, Views.TBL_RETURN_ORDER,
				Views.COL_RETURN_DETAIL_RETURN_ID, Views.COL_RETURN_ORDER_ID, Views.COL_ORDER_STATUS, periodCondition2,
				timeFormat2);

		return db.queryForList(sql);
	}

	private String getTimeFormat(String period) {
		return switch (period) {
		case "monthly" -> "FORMAT(o." + Views.COL_ORDER_DATE + ", 'yyyy-MM')";
		case "quarterly" ->
			"CONCAT(YEAR(o." + Views.COL_ORDER_DATE + "), '-Q', DATEPART(QUARTER, o." + Views.COL_ORDER_DATE + "))";
		case "yearly" -> "CAST(YEAR(o." + Views.COL_ORDER_DATE + ") AS VARCHAR)";
		default -> throw new IllegalArgumentException("Invalid period: " + period);
		};
	}

	private String getPeriodCondition(String period) {
		return switch (period) {
		case "monthly" ->
			"o." + Views.COL_ORDER_DATE + " >= DATEADD(MONTH, -11, DATEADD(MONTH, DATEDIFF(MONTH, 0, GETDATE()), 0))";
		case "quarterly" -> "o." + Views.COL_ORDER_DATE
				+ " >= DATEADD(QUARTER, -3, DATEADD(QUARTER, DATEDIFF(QUARTER, 0, GETDATE()), 0))";
		case "yearly" ->
			"o." + Views.COL_ORDER_DATE + " >= DATEADD(YEAR, -4, DATEADD(YEAR, DATEDIFF(YEAR, 0, GETDATE()), 0))";
		default -> throw new IllegalArgumentException("Invalid period: " + period);
		};
	}

	public List<Map<String, Object>> getTopProducts(String period) {
		String timeFormat = getTimeFormat(period);
		String periodCondition = getPeriodCondition(period);

		String sql = """
				WITH ProductRevenue AS (
				    SELECT
				        %s as timePeriod,
				        p.%s as productName,
				        SUM(od.%s) as unitsSold,
				        SUM(od.%s * od.%s) as revenue,
				        ROW_NUMBER() OVER(PARTITION BY %s ORDER BY SUM(od.%s * od.%s) DESC) as rn
				    FROM [%s] p
				    JOIN [%s] od ON p.%s = od.%s
				    JOIN [%s] o ON od.%s = o.%s
				    WHERE o.%s = 'Completed'
				        AND %s
				    GROUP BY %s, p.%s
				)
				SELECT
				    timePeriod,
				    productName,
				    CAST(revenue as decimal(18,2)) as revenue,
				    unitsSold
				FROM ProductRevenue
				WHERE rn <= 10
				ORDER BY timePeriod DESC, revenue DESC
				""".formatted(timeFormat, Views.COL_PRODUCT_NAME, Views.COL_ORDER_DETAIL_QUANTITY,
				Views.COL_ORDER_DETAIL_PRICE, Views.COL_ORDER_DETAIL_QUANTITY, timeFormat, Views.COL_ORDER_DETAIL_PRICE,
				Views.COL_ORDER_DETAIL_QUANTITY, Views.TBL_PRODUCT, Views.TBL_ORDER_DETAIL, Views.COL_PRODUCT_ID,
				Views.COL_ORDER_DETAIL_PRODUCT_ID, Views.TBL_ORDER, Views.COL_ORDER_DETAIL_ORDER_ID, Views.COL_ORDER_ID,
				Views.COL_ORDER_STATUS, periodCondition, timeFormat, Views.COL_PRODUCT_NAME);

		return db.queryForList(sql);
	}

	public List<Map<String, Object>> getProductSummaryTrend(String period) {
		String timeFormat = getTimeFormat(period);
		String periodCondition = getPeriodCondition(period);

		String sql = """
				WITH OrderStats AS (
				    SELECT
				        %s as timePeriod,
				        SUM(od.%s * od.%s * (1 - ISNULL(o.%s, 0)/100.0)) as revenue,
				        SUM(od.%s) as unitsSold
				    FROM [%s] od
				    JOIN [%s] o ON od.%s = o.%s
				    WHERE o.%s = 'Completed'
				        AND %s
				    GROUP BY %s
				)
				SELECT
				    timePeriod,
				    revenue,
				    unitsSold,
				    CAST(revenue AS DECIMAL(10,2)) / NULLIF(unitsSold, 0) as avgPrice
				FROM OrderStats
				ORDER BY timePeriod
				""".formatted(timeFormat, Views.COL_ORDER_DETAIL_PRICE, Views.COL_ORDER_DETAIL_QUANTITY,
				Views.COL_ORDER_DISCOUNT, Views.COL_ORDER_DETAIL_QUANTITY, Views.TBL_ORDER_DETAIL, Views.TBL_ORDER,
				Views.COL_ORDER_DETAIL_ORDER_ID, Views.COL_ORDER_ID, Views.COL_ORDER_STATUS, periodCondition,
				timeFormat);

		return db.queryForList(sql);
	}

	public List<Map<String, Object>> getWarehouseRevenue(String period) {
	    String timeFormat = switch (period) {
	        case "monthly" -> "FORMAT(DATEFROMPARTS(YEAR(o.[" + Views.COL_ORDER_DATE + "]), MONTH(o.[" + Views.COL_ORDER_DATE + "]), 1), 'yyyy-MM')";
	        case "quarterly" -> "CAST(YEAR(o.[" + Views.COL_ORDER_DATE + "]) AS VARCHAR) + '-Q' + CAST(DATEPART(QUARTER, o.[" + Views.COL_ORDER_DATE + "]) AS VARCHAR)";
	        case "yearly" -> "CAST(YEAR(o.[" + Views.COL_ORDER_DATE + "]) AS VARCHAR)";
	        default -> throw new IllegalArgumentException("Invalid period: " + period);
	    };

	    String periodCondition = getPeriodCondition(period);

	    String sql = """
	        WITH AllPeriods AS (
	            SELECT DISTINCT
	                %s as timePeriod
	            FROM [%s] o
	            WHERE o.[%s] = 'Completed'
	                AND %s
	        ),
	        TotalRevenue AS (
	            SELECT 
	                %s as timePeriod,
	                SUM(od.Price * od.Quantity) as GrossRevenue,
	                COALESCE(SUM(CASE 
	                    WHEN ro.[%s] IN ('Accepted', 'Completed') 
	                    THEN rod.Amount 
	                    ELSE 0 
	                END), 0) as ReturnAmount
	            FROM [%s] o
	            JOIN [%s] od ON o.[%s] = od.[%s]
	            LEFT JOIN [%s] rod ON od.Id = rod.Order_Detail_Id
	            LEFT JOIN [%s] ro ON rod.Return_Order_Id = ro.Id
	            WHERE o.[%s] = 'Completed'
	                AND %s
	            GROUP BY %s
	        ),
	        WarehouseStats AS (
	            SELECT
	                %s as timePeriod,
	                w.Name as warehouseName,
	                w.Id as warehouseId,
	                SUM(od.Price * od.Quantity) as GrossRevenue,
	                COALESCE(SUM(CASE 
	                    WHEN ro.[%s] IN ('Accepted', 'Completed') 
	                    THEN rod.Amount 
	                    ELSE 0 
	                END), 0) as ReturnAmount,
	                COUNT(DISTINCT o.[%s]) as totalOrders,
	                SUM(od.[%s]) as totalUnits
	            FROM [Warehouse] w
	            JOIN [%s] o ON w.Id = o.[%s]
	            JOIN [%s] od ON o.[%s] = od.[%s]
	            LEFT JOIN [%s] rod ON od.Id = rod.Order_Detail_Id
	            LEFT JOIN [%s] ro ON rod.Return_Order_Id = ro.Id
	            WHERE o.[%s] = 'Completed'
	                AND %s
	            GROUP BY %s, w.Id, w.Name
	        )
	        SELECT
	            ws.timePeriod,
	            ws.warehouseName,
	            ws.warehouseId,
	            ws.GrossRevenue - ws.ReturnAmount as NetRevenue,
	            ws.totalOrders,
	            ws.totalUnits,
	            CAST(CASE
	                WHEN tr.GrossRevenue - tr.ReturnAmount = 0 THEN 0
	                ELSE (ws.GrossRevenue - ws.ReturnAmount) * 100.0 /
	                    NULLIF(tr.GrossRevenue - tr.ReturnAmount, 0)
	            END AS DECIMAL(10,2)) as percentage
	        FROM WarehouseStats ws
	        JOIN TotalRevenue tr ON ws.timePeriod = tr.timePeriod
	        ORDER BY ws.timePeriod, (ws.GrossRevenue - ws.ReturnAmount) DESC
	    """.formatted(
	        timeFormat, Views.TBL_ORDER, Views.COL_ORDER_STATUS, periodCondition,
	        timeFormat, Views.COL_RETURN_ORDER_STATUS,
	        Views.TBL_ORDER, Views.TBL_ORDER_DETAIL, Views.COL_ORDER_ID, Views.COL_ORDER_DETAIL_ORDER_ID,
	        Views.TBL_RETURN_ORDER_DETAIL, Views.TBL_RETURN_ORDER,
	        Views.COL_ORDER_STATUS, periodCondition, timeFormat,
	        
	        timeFormat, Views.COL_RETURN_ORDER_STATUS,
	        Views.COL_ORDER_ID, Views.COL_ORDER_DETAIL_QUANTITY,
	        Views.TBL_ORDER, Views.COL_ORDER_WAREHOUSE_ID,
	        Views.TBL_ORDER_DETAIL, Views.COL_ORDER_ID, Views.COL_ORDER_DETAIL_ORDER_ID,
	        Views.TBL_RETURN_ORDER_DETAIL, Views.TBL_RETURN_ORDER,
	        Views.COL_ORDER_STATUS, periodCondition,
	        timeFormat
	    );


	    return db.queryForList(sql);
	}

	private String getPeriodCondition1(String period) {
		return switch (period) {
		case "monthly" -> "YEAR(o.Date) = YEAR(GETDATE()) AND MONTH(o.Date) = MONTH(GETDATE())";
		case "quarterly" -> """
				YEAR(o.Date) = YEAR(GETDATE()) AND
				MONTH(o.Date) BETWEEN MONTH(DATEADD(MONTH, -2, GETDATE())) AND MONTH(GETDATE())
				""";
		case "yearly" -> "YEAR(o.Date) = YEAR(GETDATE())"; // Lấy theo năm hiện tại
		default -> "YEAR(o.Date) = YEAR(GETDATE()) AND MONTH(o.Date) = MONTH(GETDATE())";
		};
	}

	public List<Map<String, Object>> getProvinceRevenue(String period) {
		String timeFormat = getTimeFormat(period);
		String periodCondition = getPeriodCondition1(period);

		String sql = """
				WITH CompletedOrders AS (
				    SELECT
				        %s as timePeriod,
				        o.[%s] as provinceId,
				        o.[%s] as orderId,
				        CAST(o.[%s] as DECIMAL(10,2)) as totalAmount,
				        o.[%s] as customerId
				    FROM [%s] o
				    WHERE o.[%s] = 'Completed'
				        AND o.[%s] IS NOT NULL
				        AND %s
				),
				TimeRevenue AS (
				    SELECT
				        timePeriod,
				        SUM(totalAmount) - COALESCE(SUM(CASE
				            WHEN ro.[%s] = 'Accepted' THEN CAST(ro.[%s] as DECIMAL(10,2))
				            ELSE 0
				        END), 0) as total_revenue
				    FROM CompletedOrders co
				    LEFT JOIN [%s] od ON co.orderId = od.[%s]
				    LEFT JOIN [%s] rod ON od.[%s] = rod.[%s]
				    LEFT JOIN [%s] ro ON ro.[%s] = rod.[%s]
				    GROUP BY timePeriod
				),
				ProvinceStats AS (
				    SELECT
				        co.timePeriod,
				        co.provinceId,
				        COUNT(DISTINCT co.orderId) as totalOrders,
				        SUM(co.totalAmount) - COALESCE(SUM(CASE
				            WHEN ro.[%s] = 'Accepted' THEN CAST(ro.[%s] as DECIMAL(10,2))
				            ELSE 0
				        END), 0) as revenue,
				        COUNT(DISTINCT co.customerId) as uniqueCustomers
				    FROM CompletedOrders co
				    LEFT JOIN [%s] od ON co.orderId = od.[%s]
				    LEFT JOIN [%s] rod ON od.[%s] = rod.[%s]
				    LEFT JOIN [%s] ro ON ro.[%s] = rod.[%s]
				    GROUP BY co.timePeriod, co.provinceId
				)
				SELECT
				    ps.timePeriod,
				    ps.provinceId,
				    ps.totalOrders,
				    CAST(ps.revenue as DECIMAL(10,2)) as revenue,
				    ps.uniqueCustomers,
				    CAST(CASE
				        WHEN tr.total_revenue = 0 THEN 0
				        ELSE ps.revenue * 100.0 / tr.total_revenue
				    END AS DECIMAL(10,2)) as percentage
				FROM ProvinceStats ps
				JOIN TimeRevenue tr ON ps.timePeriod = tr.timePeriod
				ORDER BY ps.timePeriod DESC, ps.revenue DESC
				""".formatted(timeFormat, // 1
				Views.COL_ORDER_PROVINCE_ID, // 2
				Views.COL_ORDER_ID, // 3
				Views.COL_ORDER_TOTALAMOUNT, // 4
				Views.COL_ORDER_CUSTOMER_ID, // 5
				Views.TBL_ORDER, // 6
				Views.COL_ORDER_STATUS, // 7
				Views.COL_ORDER_PROVINCE_ID, // 8
				periodCondition, // 9
				Views.COL_RETURN_ORDER_STATUS, // 10
				Views.COL_RETURN_ORDER_FINAL_AMOUNT, // 11
				Views.TBL_ORDER_DETAIL, // 12
				Views.COL_ORDER_DETAIL_ORDER_ID, // 13
				Views.TBL_RETURN_ORDER_DETAIL, // 14
				Views.COL_ORDER_DETAIL_ID, // 15
				Views.COL_RETURN_DETAIL_ORDER_DETAIL_ID, // 16
				Views.TBL_RETURN_ORDER, // 17
				Views.COL_RETURN_ORDER_ID, // 18
				Views.COL_RETURN_DETAIL_RETURN_ID, // 19
				Views.COL_RETURN_ORDER_STATUS, // 20
				Views.COL_RETURN_ORDER_FINAL_AMOUNT, // 21
				Views.TBL_ORDER_DETAIL, // 22
				Views.COL_ORDER_DETAIL_ORDER_ID, // 23
				Views.TBL_RETURN_ORDER_DETAIL, // 24
				Views.COL_ORDER_DETAIL_ID, // 25
				Views.COL_RETURN_DETAIL_ORDER_DETAIL_ID, // 26
				Views.TBL_RETURN_ORDER, // 27
				Views.COL_RETURN_ORDER_ID, // 28
				Views.COL_RETURN_DETAIL_RETURN_ID // 29
		);

		return db.queryForList(sql);
	}

	public List<Map<String, Object>> getGrossProfitAnalysis(String period) {
	    String sql = """
	        WITH ProductCostPrice AS (
    SELECT DISTINCT
        wrd.Product_Id as productId,
        FIRST_VALUE(wrd.Wh_price) OVER (
            PARTITION BY wrd.Product_Id 
            ORDER BY wr.Date DESC, wrd.Wh_receiptId DESC
        ) as latestCostPrice
    FROM [%s] wrd
    JOIN [%s] wr ON wrd.%s = wr.%s
    WHERE wrd.%s = 'Active'
),
OrderData AS (
    SELECT 
        FORMAT(o.%s, 'yyyy-MM') as timePeriod,
        od.%s as productId,
        p.%s as productName,
        SUM(od.%s) as soldQuantity,
        SUM(od.%s * od.%s) as revenue
    FROM [%s] o
    JOIN [%s] od ON o.%s = od.%s
    JOIN [%s] p ON od.%s = p.%s
    WHERE o.%s = 'Completed'
        AND o.%s >= DATEADD(MONTH, -11, DATEADD(MONTH, DATEDIFF(MONTH, 0, GETDATE()), 0))
    GROUP BY 
        FORMAT(o.%s, 'yyyy-MM'),
        od.%s,
        p.%s
),
ReturnData AS (
    SELECT 
        FORMAT(o.%s, 'yyyy-MM') as timePeriod,
        od.%s as productId,
        SUM(rod.%s) as returnQuantity,
        SUM(rod.%s) as returnAmount
    FROM [%s] ro
    JOIN [%s] rod ON ro.%s = rod.%s
    JOIN [%s] o ON ro.%s = o.%s
    JOIN [%s] od ON rod.%s = od.%s
    WHERE ro.%s IN ('Accepted', 'Completed')
    GROUP BY 
        FORMAT(o.%s, 'yyyy-MM'),
        od.%s
),
SalesData AS (
    SELECT 
        od.timePeriod,
        od.productId,
        od.productName,
        od.soldQuantity - COALESCE(rd.returnQuantity, 0) as quantitySold,
        od.revenue - COALESCE(rd.returnAmount, 0) as revenue,
        (od.soldQuantity - COALESCE(rd.returnQuantity, 0)) * pc.latestCostPrice as totalCost
    FROM OrderData od
    LEFT JOIN ReturnData rd ON rd.timePeriod = od.timePeriod 
        AND rd.productId = od.productId
    LEFT JOIN ProductCostPrice pc ON od.productId = pc.productId
)
SELECT 
    s.timePeriod,
    s.productId,
    s.productName,
    s.quantitySold,
    CAST(s.revenue as DECIMAL(18,2)) as grossRevenue,
    CAST(s.totalCost as DECIMAL(18,2)) as totalCost,
    CAST(s.revenue - s.totalCost as DECIMAL(18,2)) as grossProfit,
    CAST((s.revenue - s.totalCost) * 100.0 / NULLIF(s.revenue, 0) as DECIMAL(18,2)) as grossProfitMargin
FROM SalesData s
WHERE s.revenue > 0
ORDER BY s.timePeriod DESC, grossProfit DESC
	    """.formatted(
	    	    // ProductCostPrice CTE
	    	    Views.TBL_WAREHOUSE_RECEIPT_DETAIL,               // [Warehouse_receipt_detail]
	    	    Views.TBL_WAREHOUSE_RECEIPT,                      // [Warehouse_receipt]
	    	    Views.COL_DETAIL_WAREHOUSE_RECEIPT_ID,            // wrd.Wh_receiptId
	    	    Views.COL_WAREHOUSE_RECEIPT_ID,                   // wr.Id
	    	    Views.COL_WAREHOUSE_RECEIPT_DETAILS_STATUS,       // wrd.Status

	    	    // OrderData CTE
	    	    Views.COL_ORDER_DATE,                            // o.Date
	    	    Views.COL_ORDER_DETAIL_PRODUCT_ID,               // od.Product_Id
	    	    Views.COL_PRODUCT_NAME,                          // p.Product_name
	    	    Views.COL_ORDER_DETAIL_QUANTITY,                 // od.Quantity
	    	    Views.COL_ORDER_DETAIL_QUANTITY,                 // od.Quantity
	    	    Views.COL_ORDER_DETAIL_PRICE,                    // od.Price
	    	    Views.TBL_ORDER,                                 // [Order]
	    	    Views.TBL_ORDER_DETAIL,                          // [Order_detail]
	    	    Views.COL_ORDER_ID,                              // o.Id
	    	    Views.COL_ORDER_DETAIL_ORDER_ID,                 // od.Order_Id
	    	    Views.TBL_PRODUCT,                               // [Product]
	    	    Views.COL_ORDER_DETAIL_PRODUCT_ID,               // od.Product_Id
	    	    Views.COL_PRODUCT_ID,                            // p.Id
	    	    Views.COL_ORDER_STATUS,                          // o.Status
	    	    Views.COL_ORDER_DATE,                            // o.Date
	    	    Views.COL_ORDER_DATE,                            // o.Date
	    	    Views.COL_ORDER_DETAIL_PRODUCT_ID,               // od.Product_Id
	    	    Views.COL_PRODUCT_NAME,                          // p.Product_name

	    	    // ReturnData CTE
	    	    Views.COL_ORDER_DATE,                            // o.Date
	    	    Views.COL_ORDER_DETAIL_PRODUCT_ID,               // od.Product_Id
	    	    Views.COL_RETURN_DETAIL_QUANTITY,                // rod.Quantity
	    	    Views.COL_RETURN_DETAIL_AMOUNT,                  // rod.Amount
	    	    Views.TBL_RETURN_ORDER,                          // [Return_Order]
	    	    Views.TBL_RETURN_ORDER_DETAIL,                   // [Return_Order_Detail]
	    	    Views.COL_RETURN_ORDER_ID,                       // ro.Id
	    	    Views.COL_RETURN_DETAIL_RETURN_ID,               // rod.Return_Order_Id
	    	    Views.TBL_ORDER,                                 // [Order]
	    	    Views.COL_RETURN_ORDER_ORDER_ID,                 // ro.Order_Id
	    	    Views.COL_ORDER_ID,                              // o.Id
	    	    Views.TBL_ORDER_DETAIL,                          // [Order_detail]
	    	    Views.COL_RETURN_DETAIL_ORDER_DETAIL_ID,         // rod.Order_Detail_Id
	    	    Views.COL_ORDER_DETAIL_ID,                       // od.Id
	    	    Views.COL_RETURN_ORDER_STATUS,                   // ro.Status
	    	    Views.COL_ORDER_DATE,                            // o.Date
	    	    Views.COL_ORDER_DETAIL_PRODUCT_ID                // od.Product_Id
	    	);

	    List<Map<String, Object>> results = db.queryForList(sql);

	    // In kết quả chi tiết
	    System.out.println("\n=== GROSS PROFIT ANALYSIS ===");
	    System.out.println(String.format("%-10s %-20s %-10s %-12s %-12s %-10s %-10s",
	        "Time", "Product", "Units", "Revenue", "Cost", "Profit", "Margin%"));
	    
	    for (Map<String, Object> row : results) {
	        System.out.println(String.format("%-10s %-20s %-10d %-12.2f %-12.2f %-10.2f %-10.2f",
	            row.get("timePeriod"),
	            truncateString(row.get("productName").toString(), 20),
	            ((Number)row.get("quantitySold")).intValue(),
	            ((Number)row.get("grossRevenue")).doubleValue(),
	            ((Number)row.get("totalCost")).doubleValue(),
	            ((Number)row.get("grossProfit")).doubleValue(),
	            ((Number)row.get("grossProfitMargin")).doubleValue()));
	    }

	    // Tính tổng theo tháng
	    System.out.println("\n=== MONTHLY SUMMARY ===");
	    Map<String, Map<String, Object>> monthlySummary = new HashMap<>();
	    
	    for (Map<String, Object> row : results) {
	        String month = row.get("timePeriod").toString();
	        monthlySummary.computeIfAbsent(month, k -> new HashMap<>());
	        
	        Map<String, Object> summary = monthlySummary.get(month);
	        summary.merge("units", ((Number)row.get("quantitySold")).intValue(), (a, b) -> 
	            ((Number)a).intValue() + ((Number)b).intValue());
	        summary.merge("revenue", ((Number)row.get("grossRevenue")).doubleValue(), (a, b) -> 
	            ((Number)a).doubleValue() + ((Number)b).doubleValue());
	        summary.merge("cost", ((Number)row.get("totalCost")).doubleValue(), (a, b) -> 
	            ((Number)a).doubleValue() + ((Number)b).doubleValue());
	        summary.merge("profit", ((Number)row.get("grossProfit")).doubleValue(), (a, b) -> 
	            ((Number)a).doubleValue() + ((Number)b).doubleValue());
	    }

	    System.out.println(String.format("%-10s %-10s %-12s %-12s %-12s %-10s",
	        "Month", "Units", "Revenue", "Cost", "Profit", "Margin%"));
	    
	    for (Map.Entry<String, Map<String, Object>> entry : monthlySummary.entrySet()) {
	        int units = ((Number)entry.getValue().get("units")).intValue();
	        double revenue = ((Number)entry.getValue().get("revenue")).doubleValue();
	        double cost = ((Number)entry.getValue().get("cost")).doubleValue();
	        double profit = ((Number)entry.getValue().get("profit")).doubleValue();
	        double margin = revenue > 0 ? (profit * 100.0 / revenue) : 0;
	        
	        System.out.println(String.format("%-10s %-10d %-12.2f %-12.2f %-12.2f %-10.2f",
	            entry.getKey(), units, revenue, cost, profit, margin));
	    }
	    
	    return results;
	}

	private String truncateString(String str, int length) {
	    if (str == null) return "";
	    if (str.length() <= length) return str;
	    return str.substring(0, length - 3) + "...";
	}

}
