package com.admin.repository;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.utils.Views;

@Repository
public class StatisticsRepository {

	@Autowired
	JdbcTemplate db;

	public List<Map<String, Object>> getProductRevenue(String period, Long productId) {
		String timeFormat = switch (period) {
		case "monthly" -> "FORMAT(DATEFROMPARTS(YEAR(o.[" + Views.COL_ORDER_DATE + "]), MONTH(o.["
				+ Views.COL_ORDER_DATE + "]), 1), 'yyyy-MM')";
		case "quarterly" -> "CAST(YEAR(o.[" + Views.COL_ORDER_DATE
				+ "]) AS VARCHAR) + '-Q' + CAST(DATEPART(QUARTER, o.[" + Views.COL_ORDER_DATE + "]) AS VARCHAR)";
		case "yearly" -> "CAST(YEAR(o.[" + Views.COL_ORDER_DATE + "]) AS VARCHAR)";
		default -> throw new IllegalArgumentException("Invalid period: " + period);
		};

		String groupBy = switch (period) {
		case "monthly" -> "YEAR(o.[" + Views.COL_ORDER_DATE + "]), MONTH(o.[" + Views.COL_ORDER_DATE + "])";
		case "quarterly" ->
			"YEAR(o.[" + Views.COL_ORDER_DATE + "]), DATEPART(QUARTER, o.[" + Views.COL_ORDER_DATE + "])";
		case "yearly" -> "YEAR(o.[" + Views.COL_ORDER_DATE + "])";
		default -> throw new IllegalArgumentException("Invalid period: " + period);
		};

		String periodCondition = getPeriodCondition(period);

		String sql = """
				WITH OrderRevenue AS (
				    SELECT
				        %s as month,
				        o.Id as OrderId,
				        od.Id as OrderDetailId,
				        od.Price * od.Quantity as revenue
				    FROM [%s] o
				    JOIN [%s] od ON o.[%s] = od.[%s]
				    WHERE o.[%s] = 'Completed'
				        AND od.[%s] = %d
				        AND %s
				),
				ReturnRevenue AS (
				    SELECT
				        %s as month,
				        rod.Order_Detail_Id,
				        SUM(rod.Amount) as return_amount
				    FROM [%s] o
				    JOIN [%s] ro ON ro.[%s] = o.[%s]
				    JOIN [%s] rod ON rod.[%s] = ro.[%s]
				    JOIN [%s] od ON od.Id = rod.Order_Detail_Id
				    WHERE ro.Status IN ('Completed', 'Accepted')
				        AND od.[%s] = %d
				    GROUP BY %s, rod.Order_Detail_Id
				),
				SelectedRevenue AS (
				    SELECT
				        o.month,
				        SUM(o.revenue) as gross_revenue,
				        ISNULL(SUM(r.return_amount), 0) as return_amount
				    FROM OrderRevenue o
				    LEFT JOIN ReturnRevenue r ON r.month = o.month AND r.Order_Detail_Id = o.OrderDetailId
				    GROUP BY o.month
				),
				TotalRevenue AS (
				    SELECT
				        %s as month,
				        SUM(o.totalAmount) as gross_revenue,
				        COALESCE(SUM(CASE
				            WHEN ro.Status IN ('Completed', 'Accepted')
				            THEN ro.Final_Amount
				            ELSE 0
				        END), 0) as return_amount
				    FROM [%s] o
				    LEFT JOIN [%s] ro ON ro.[%s] = o.[%s]
				    WHERE o.[%s] = 'Completed'
				        AND %s
				    GROUP BY %s
				)
				SELECT
				    t.month as timePeriod,
				    ISNULL(s.gross_revenue - s.return_amount, 0) as SelectedRevenue,
				    (t.gross_revenue - t.return_amount) - ISNULL(s.gross_revenue - s.return_amount, 0) as OtherRevenue,
				    t.gross_revenue - t.return_amount as TotalRevenue,
				    CAST(ISNULL(s.gross_revenue - s.return_amount, 0) * 100.0 /
				        NULLIF(t.gross_revenue - t.return_amount, 0) as DECIMAL(10,2)) as SelectedPercentage,
				    CAST(((t.gross_revenue - t.return_amount) - ISNULL(s.gross_revenue - s.return_amount, 0)) * 100.0 /
				        NULLIF(t.gross_revenue - t.return_amount, 0) as DECIMAL(10,2)) as OtherPercentage
				FROM TotalRevenue t
				LEFT JOIN SelectedRevenue s ON t.month = s.month
				ORDER BY t.month ASC
				""".formatted(
				// OrderRevenue parameters
				timeFormat, Views.TBL_ORDER, Views.TBL_ORDER_DETAIL, Views.COL_ORDER_ID,
				Views.COL_ORDER_DETAIL_ORDER_ID, Views.COL_ORDER_STATUS, Views.COL_ORDER_DETAIL_PRODUCT_ID, productId,
				periodCondition,

				// ReturnRevenue parameters
				timeFormat, Views.TBL_ORDER, Views.TBL_RETURN_ORDER, Views.COL_RETURN_ORDER_ORDER_ID,
				Views.COL_ORDER_ID, Views.TBL_RETURN_ORDER_DETAIL, Views.COL_RETURN_DETAIL_RETURN_ID,
				Views.COL_RETURN_ORDER_ID, Views.TBL_ORDER_DETAIL, Views.COL_ORDER_DETAIL_PRODUCT_ID, productId,
				groupBy,

				// TotalRevenue parameters
				timeFormat, Views.TBL_ORDER, Views.TBL_RETURN_ORDER, Views.COL_RETURN_ORDER_ORDER_ID,
				Views.COL_ORDER_ID, Views.COL_ORDER_STATUS, periodCondition, groupBy);

		return db.queryForList(sql);
	}

	public List<Map<String, Object>> getCategoryRevenue(String period, Long categoryId) {
		String timeFormat = switch (period) {
		case "monthly" -> "FORMAT(DATEFROMPARTS(YEAR(o.[" + Views.COL_ORDER_DATE + "]), MONTH(o.["
				+ Views.COL_ORDER_DATE + "]), 1), 'yyyy-MM')";
		case "quarterly" -> "CAST(YEAR(o.[" + Views.COL_ORDER_DATE
				+ "]) AS VARCHAR) + '-Q' + CAST(DATEPART(QUARTER, o.[" + Views.COL_ORDER_DATE + "]) AS VARCHAR)";
		case "yearly" -> "CAST(YEAR(o.[" + Views.COL_ORDER_DATE + "]) AS VARCHAR)";
		default -> throw new IllegalArgumentException("Invalid period: " + period);
		};

		String groupBy = switch (period) {
		case "monthly" -> "YEAR(o.[" + Views.COL_ORDER_DATE + "]), MONTH(o.[" + Views.COL_ORDER_DATE + "])";
		case "quarterly" ->
			"YEAR(o.[" + Views.COL_ORDER_DATE + "]), DATEPART(QUARTER, o.[" + Views.COL_ORDER_DATE + "])";
		case "yearly" -> "YEAR(o.[" + Views.COL_ORDER_DATE + "])";
		default -> throw new IllegalArgumentException("Invalid period: " + period);
		};
		String periodCondition = getPeriodCondition(period);

		String sql = """
				WITH OrderRevenue AS (
				    SELECT
				        %s as month,
				        o.Id as OrderId,
				        SUM(od.Price * od.Quantity) as revenue
				    FROM [%s] o
				    JOIN [%s] od ON o.[%s] = od.[%s]
				    JOIN [%s] p ON p.[%s] = od.[%s]
				    WHERE o.[%s] = 'Completed'
				        AND p.[%s] = %d
				        AND %s
				    GROUP BY %s, o.Id
				),
				ReturnRevenue AS (
				    SELECT
				        %s as month,
				        o.Id as OrderId,
				        SUM(CASE
				            WHEN ro.Status = 'Accepted'
				            THEN (od.Price * od.Quantity * ro.Final_Amount / NULLIF(o.totalAmount, 0))
				            ELSE 0
				        END) as return_amount
				    FROM [%s] o
				    JOIN [%s] od ON o.[%s] = od.[%s]
				    JOIN [%s] p ON p.[%s] = od.[%s]
				    LEFT JOIN [%s] ro ON ro.[%s] = o.[%s]
				    WHERE o.[%s] = 'Completed'
				        AND p.[%s] = %d
				        AND %s
				    GROUP BY %s, o.Id, o.totalAmount
				),
				SelectedRevenue AS (
				    SELECT
				        o.month,
				        SUM(o.revenue) as gross_revenue,
				        ISNULL(SUM(r.return_amount), 0) as return_amount
				    FROM OrderRevenue o
				    LEFT JOIN ReturnRevenue r ON r.month = o.month AND r.OrderId = o.OrderId
				    GROUP BY o.month
				),
				TotalRevenue AS (
				    SELECT
				        %s as month,
				        SUM(o.totalAmount) as gross_revenue,
				        SUM(CASE
				            WHEN ro.Status = 'Accepted' THEN ro.Final_Amount
				            ELSE 0
				        END) as return_amount
				    FROM [%s] o
				    LEFT JOIN [%s] ro ON ro.[%s] = o.[%s]
				    WHERE o.[%s] = 'Completed'
				        AND %s
				    GROUP BY %s
				)
				SELECT
				    t.month as timePeriod,
				    ISNULL(s.gross_revenue - s.return_amount, 0) as SelectedRevenue,
				    (t.gross_revenue - t.return_amount) - ISNULL(s.gross_revenue - s.return_amount, 0) as OtherRevenue,
				    t.gross_revenue - t.return_amount as TotalRevenue,
				    CAST(ISNULL(s.gross_revenue - s.return_amount, 0) * 100.0 /
				        NULLIF(t.gross_revenue - t.return_amount, 0) as DECIMAL(10,2)) as SelectedPercentage,
				    CAST(((t.gross_revenue - t.return_amount) - ISNULL(s.gross_revenue - s.return_amount, 0)) * 100.0 /
				        NULLIF(t.gross_revenue - t.return_amount, 0) as DECIMAL(10,2)) as OtherPercentage
				FROM TotalRevenue t
				LEFT JOIN SelectedRevenue s ON t.month = s.month
				ORDER BY t.month ASC
				""".formatted(
				// OrderRevenue parameters
				timeFormat, Views.TBL_ORDER, Views.TBL_ORDER_DETAIL, Views.COL_ORDER_ID,
				Views.COL_ORDER_DETAIL_ORDER_ID, Views.TBL_PRODUCT, Views.COL_PRODUCT_ID,
				Views.COL_ORDER_DETAIL_PRODUCT_ID, Views.COL_ORDER_STATUS, Views.COL_PRODUCT_CATE_ID, categoryId,
				periodCondition, timeFormat + ", " + groupBy,

				// ReturnRevenue parameters
				timeFormat, Views.TBL_ORDER, Views.TBL_ORDER_DETAIL, Views.COL_ORDER_ID,
				Views.COL_ORDER_DETAIL_ORDER_ID, Views.TBL_PRODUCT, Views.COL_PRODUCT_ID,
				Views.COL_ORDER_DETAIL_PRODUCT_ID, Views.TBL_RETURN_ORDER, Views.COL_RETURN_ORDER_ORDER_ID,
				Views.COL_ORDER_ID, Views.COL_ORDER_STATUS, Views.COL_PRODUCT_CATE_ID, categoryId, periodCondition,
				timeFormat + ", " + groupBy,

				// TotalRevenue parameters
				timeFormat, Views.TBL_ORDER, Views.TBL_RETURN_ORDER, Views.COL_RETURN_ORDER_ORDER_ID,
				Views.COL_ORDER_ID, Views.COL_ORDER_STATUS, periodCondition, timeFormat + ", " + groupBy);

		return db.queryForList(sql);
	}


	public List<Map<String, Object>> getBrandRevenue(String period, Long brandId) {
		String timeFormat = switch (period) {
		case "monthly" -> "FORMAT(DATEFROMPARTS(YEAR(o.[" + Views.COL_ORDER_DATE + "]), MONTH(o.["
				+ Views.COL_ORDER_DATE + "]), 1), 'yyyy-MM')";
		case "quarterly" -> "CAST(YEAR(o.[" + Views.COL_ORDER_DATE
				+ "]) AS VARCHAR) + '-Q' + CAST(DATEPART(QUARTER, o.[" + Views.COL_ORDER_DATE + "]) AS VARCHAR)";
		case "yearly" -> "CAST(YEAR(o.[" + Views.COL_ORDER_DATE + "]) AS VARCHAR)";
		default -> throw new IllegalArgumentException("Invalid period: " + period);
		};

		String groupBy = switch (period) {
		case "monthly" -> "YEAR(o.[" + Views.COL_ORDER_DATE + "]), MONTH(o.[" + Views.COL_ORDER_DATE + "])";
		case "quarterly" ->
			"YEAR(o.[" + Views.COL_ORDER_DATE + "]), DATEPART(QUARTER, o.[" + Views.COL_ORDER_DATE + "])";
		case "yearly" -> "YEAR(o.[" + Views.COL_ORDER_DATE + "])";
		default -> throw new IllegalArgumentException("Invalid period: " + period);
		};
		String periodCondition = getPeriodCondition(period);

		String sql = """
				WITH OrderRevenue AS (
				    SELECT
				        %s as month,
				        o.Id as OrderId,
				        SUM(od.Price * od.Quantity) as revenue
				    FROM [%s] o
				    JOIN [%s] od ON o.[%s] = od.[%s]
				    JOIN [%s] p ON p.[%s] = od.[%s]
				    WHERE o.[%s] = 'Completed'
				        AND p.[%s] = %d
				        AND %s
				    GROUP BY %s, o.Id
				),
				ReturnRevenue AS (
				    SELECT
				        %s as month,
				        o.Id as OrderId,
				        SUM(CASE
				            WHEN ro.Status = 'Accepted'
				            THEN (od.Price * od.Quantity * ro.Final_Amount / NULLIF(o.totalAmount, 0))
				            ELSE 0
				        END) as return_amount
				    FROM [%s] o
				    JOIN [%s] od ON o.[%s] = od.[%s]
				    JOIN [%s] p ON p.[%s] = od.[%s]
				    LEFT JOIN [%s] ro ON ro.[%s] = o.[%s]
				    WHERE o.[%s] = 'Completed'
				        AND p.[%s] = %d
				        AND %s
				    GROUP BY %s, o.Id, o.totalAmount
				),
				SelectedRevenue AS (
				    SELECT
				        o.month,
				        SUM(o.revenue) as gross_revenue,
				        ISNULL(SUM(r.return_amount), 0) as return_amount
				    FROM OrderRevenue o
				    LEFT JOIN ReturnRevenue r ON r.month = o.month AND r.OrderId = o.OrderId
				    GROUP BY o.month
				),
				TotalRevenue AS (
				    SELECT
				        %s as month,
				        SUM(o.totalAmount) as gross_revenue,
				        SUM(CASE
				            WHEN ro.Status = 'Accepted' THEN ro.Final_Amount
				            ELSE 0
				        END) as return_amount
				    FROM [%s] o
				    LEFT JOIN [%s] ro ON ro.[%s] = o.[%s]
				    WHERE o.[%s] = 'Completed'
				        AND %s
				    GROUP BY %s
				)
				SELECT
				    t.month as timePeriod,
				    ISNULL(s.gross_revenue - s.return_amount, 0) as SelectedRevenue,
				    (t.gross_revenue - t.return_amount) - ISNULL(s.gross_revenue - s.return_amount, 0) as OtherRevenue,
				    t.gross_revenue - t.return_amount as TotalRevenue,
				    CAST(ISNULL(s.gross_revenue - s.return_amount, 0) * 100.0 /
				        NULLIF(t.gross_revenue - t.return_amount, 0) as DECIMAL(10,2)) as SelectedPercentage,
				    CAST(((t.gross_revenue - t.return_amount) - ISNULL(s.gross_revenue - s.return_amount, 0)) * 100.0 /
				        NULLIF(t.gross_revenue - t.return_amount, 0) as DECIMAL(10,2)) as OtherPercentage
				FROM TotalRevenue t
				LEFT JOIN SelectedRevenue s ON t.month = s.month
				ORDER BY t.month ASC
				""".formatted(
				// OrderRevenue parameters
				timeFormat, Views.TBL_ORDER, Views.TBL_ORDER_DETAIL, Views.COL_ORDER_ID,
				Views.COL_ORDER_DETAIL_ORDER_ID, Views.TBL_PRODUCT, Views.COL_PRODUCT_ID,
				Views.COL_ORDER_DETAIL_PRODUCT_ID, Views.COL_ORDER_STATUS, Views.COL_PRODUCT_BRAND_ID, brandId,
				periodCondition, timeFormat + ", " + groupBy,

				// ReturnRevenue parameters
				timeFormat, Views.TBL_ORDER, Views.TBL_ORDER_DETAIL, Views.COL_ORDER_ID,
				Views.COL_ORDER_DETAIL_ORDER_ID, Views.TBL_PRODUCT, Views.COL_PRODUCT_ID,
				Views.COL_ORDER_DETAIL_PRODUCT_ID, Views.TBL_RETURN_ORDER, Views.COL_RETURN_ORDER_ORDER_ID,
				Views.COL_ORDER_ID, Views.COL_ORDER_STATUS, Views.COL_PRODUCT_BRAND_ID, brandId, periodCondition,
				timeFormat + ", " + groupBy,

				// TotalRevenue parameters
				timeFormat, Views.TBL_ORDER, Views.TBL_RETURN_ORDER, Views.COL_RETURN_ORDER_ORDER_ID,
				Views.COL_ORDER_ID, Views.COL_ORDER_STATUS, periodCondition, timeFormat + ", " + groupBy);

		return db.queryForList(sql);
	}

	public List<Map<String, Object>> getBestSellingProducts(String period) {
		String timeFormat = getTimeFormat(period);
		String periodCondition = getPeriodCondition(period);

		String sql = """
				WITH OrderStats AS (
				    SELECT
				        %s as timePeriod,
				        p.%s as productId,
				        p.%s as productName,
				        SUM(od.%s) as grossQuantity,
				        COALESCE(SUM(CASE
				            WHEN ro.%s IN ('Completed', 'Accepted')
				            THEN rod.%s
				            ELSE 0
				        END), 0) as returnQuantity,
				        SUM(od.%s) - COALESCE(SUM(CASE
				            WHEN ro.%s IN ('Completed', 'Accepted')
				            THEN rod.%s
				            ELSE 0
				        END), 0) as totalQuantity,
				        SUM(od.%s * od.%s) - COALESCE(SUM(CASE
				            WHEN ro.%s IN ('Completed', 'Accepted')
				            THEN rod.%s
				            ELSE 0
				        END), 0) as totalRevenue,
				        COUNT(DISTINCT o.%s) - COUNT(DISTINCT CASE
				            WHEN ro.%s IN ('Completed', 'Accepted')
				            THEN ro.%s
				        END) as orderCount
				    FROM [%s] p
				    JOIN [%s] od ON p.%s = od.%s
				    JOIN [%s] o ON od.%s = o.%s
				    LEFT JOIN [%s] rod ON od.%s = rod.%s
				    LEFT JOIN [%s] ro ON rod.%s = ro.%s
				    WHERE o.%s = 'Completed'
				        AND %s
				    GROUP BY %s, p.%s, p.%s
				),
				RankedProducts AS (
				    SELECT
				        timePeriod,
				        productName,
				        CAST(COALESCE(grossQuantity, 0) as INT) as grossQuantity,
				        CAST(COALESCE(returnQuantity, 0) as INT) as returnQuantity,
				        CAST(COALESCE(totalQuantity, 0) as INT) as totalQuantity,
				        CAST(COALESCE(totalRevenue, 0) as DECIMAL(18,2)) as revenue,
				        CAST(COALESCE(orderCount, 0) as INT) as orderCount,
				        CAST(
				            CASE
				                WHEN grossQuantity > 0
				                THEN (returnQuantity * 100.0 / grossQuantity)
				                ELSE 0
				            END
				        as DECIMAL(10,2)) as returnRate,
				        ROW_NUMBER() OVER (PARTITION BY timePeriod ORDER BY totalRevenue DESC) as rank
				    FROM OrderStats
				    WHERE totalRevenue > 0
				)
				SELECT * FROM RankedProducts
				WHERE rank <= 5
				ORDER BY timePeriod DESC, rank ASC
				""".formatted(timeFormat, Views.COL_PRODUCT_ID, Views.COL_PRODUCT_NAME, Views.COL_ORDER_DETAIL_QUANTITY,
				Views.COL_RETURN_ORDER_STATUS, Views.COL_RETURN_DETAIL_QUANTITY, Views.COL_ORDER_DETAIL_QUANTITY,
				Views.COL_RETURN_ORDER_STATUS, Views.COL_RETURN_DETAIL_QUANTITY, Views.COL_ORDER_DETAIL_PRICE,
				Views.COL_ORDER_DETAIL_QUANTITY, Views.COL_RETURN_ORDER_STATUS, Views.COL_RETURN_DETAIL_AMOUNT,
				Views.COL_ORDER_ID, Views.COL_RETURN_ORDER_STATUS, Views.COL_RETURN_ORDER_ID, Views.TBL_PRODUCT,
				Views.TBL_ORDER_DETAIL, Views.COL_PRODUCT_ID, Views.COL_ORDER_DETAIL_PRODUCT_ID, Views.TBL_ORDER,
				Views.COL_ORDER_DETAIL_ORDER_ID, Views.COL_ORDER_ID, Views.TBL_RETURN_ORDER_DETAIL,
				Views.COL_ORDER_DETAIL_ID, Views.COL_RETURN_DETAIL_ORDER_DETAIL_ID, Views.TBL_RETURN_ORDER,
				Views.COL_RETURN_DETAIL_RETURN_ID, Views.COL_RETURN_ORDER_ID, Views.COL_ORDER_STATUS, periodCondition,
				timeFormat, Views.COL_PRODUCT_ID, Views.COL_PRODUCT_NAME);

		return db.queryForList(sql);
	}

	private String getTimeFormat(String period) {
		return switch (period) {
		case "monthly" -> "FORMAT(o." + Views.COL_ORDER_DATE + ", 'yyyy-MM')";
		case "quarterly" -> "CONCAT(YEAR(o." + Views.COL_ORDER_DATE + "), '-', "
				+ "RIGHT('0' + CAST(DATEPART(QUARTER, o." + Views.COL_ORDER_DATE + ") AS VARCHAR), 2))";
		case "yearly" -> "CAST(YEAR(o." + Views.COL_ORDER_DATE + ") AS VARCHAR)";
		default -> throw new IllegalArgumentException("Invalid period: " + period);
		};
	}

	private String getPeriodCondition(String period) {
		return switch (period) {
		case "monthly" ->
			// Lấy từ tháng hiện tại của năm trước (12 tháng)
			"o." + Views.COL_ORDER_DATE + " >= DATEADD(YEAR, -1, DATEADD(MONTH, DATEDIFF(MONTH, 0, GETDATE()), 0))";
		case "quarterly" ->
			// Lấy từ quý hiện tại của năm trước (4 quý)
			"o." + Views.COL_ORDER_DATE + " >= DATEADD(YEAR, -1, DATEADD(QUARTER, DATEDIFF(QUARTER, 0, GETDATE()), 0))";
		case "yearly" ->
			// Lấy tất cả các năm
			"o." + Views.COL_ORDER_DATE + " IS NOT NULL";
		default -> throw new IllegalArgumentException("Invalid period: " + period);
		};
	}

	public List<Map<String, Object>> getTopProducts(String period) {
		String timeFormat = switch (period) {
		case "monthly" -> "FORMAT(DATEFROMPARTS(YEAR(o.[" + Views.COL_ORDER_DATE + "]), MONTH(o.["
				+ Views.COL_ORDER_DATE + "]), 1), 'yyyy-MM')";
		case "quarterly" -> "CAST(YEAR(o.[" + Views.COL_ORDER_DATE
				+ "]) AS VARCHAR) + '-Q' + CAST(DATEPART(QUARTER, o.[" + Views.COL_ORDER_DATE + "]) AS VARCHAR)";
		case "yearly" -> "CAST(YEAR(o.[" + Views.COL_ORDER_DATE + "]) AS VARCHAR)";
		default -> throw new IllegalArgumentException("Invalid period: " + period);
		};

		String periodCondition = "o.[" + Views.COL_ORDER_DATE + "] >= DATEADD(MONTH, -4, GETDATE())";

		String sql = """
				WITH AllPeriods AS (
				       SELECT DISTINCT
				           %s as timePeriod,
				           SUM(o.%s) - ISNULL((
				               SELECT SUM(ro.%s)
				               FROM [%s] ro
				               JOIN [%s] o2 ON ro.%s = o2.%s
				               WHERE ro.%s IN ('Completed', 'Accepted')
				               AND %s = %s
				           ), 0) as total_revenue
				       FROM [%s] o
				       WHERE o.%s = 'Completed'
				           AND %s
				       GROUP BY %s
				   ),
				OrderRevenue AS (
				    SELECT
				        %s as timePeriod,
				        p.%s as productId,
				        p.%s as productName,
				        SUM(od.%s * od.%s) as revenue,
				        SUM(od.%s) as quantity
				    FROM [%s] o
				    JOIN [%s] od ON o.%s = od.%s
				    JOIN [%s] p ON od.%s = p.%s
				    WHERE o.%s = 'Completed'
				        AND %s
				    GROUP BY %s, p.%s, p.%s
				),
				ReturnRevenue AS (
				    SELECT
				        %s as timePeriod,
				        od.%s as productId,
				        SUM(rod.%s) as return_amount,
				        SUM(rod.%s) as return_quantity
				    FROM [%s] o
				    JOIN [%s] ro ON ro.%s = o.%s
				    JOIN [%s] rod ON rod.%s = ro.%s
				    JOIN [%s] od ON rod.%s = od.%s
				    WHERE ro.%s IN ('Completed', 'Accepted')
				    GROUP BY %s, od.%s
				),
				ProductStats AS (
				    SELECT
				        ap.timePeriod,
				        ap.total_revenue,
				        o.productId,
				        o.productName,
				        ISNULL(o.revenue, 0) as gross_revenue,
				        ISNULL(r.return_amount, 0) as return_amount,
				        ISNULL(o.quantity, 0) - ISNULL(r.return_quantity, 0) as total_units
				    FROM AllPeriods ap
				    LEFT JOIN OrderRevenue o ON o.timePeriod = ap.timePeriod
				    LEFT JOIN ReturnRevenue r ON r.timePeriod = ap.timePeriod
				        AND r.productId = o.productId
				),
				Top5Products AS (
				    SELECT DISTINCT TOP 5
				        productId,
				        productName,
				        SUM(gross_revenue - return_amount) as total_revenue
				    FROM ProductStats
				    WHERE productId IS NOT NULL
				    GROUP BY productId, productName
				    ORDER BY total_revenue DESC
				)
				SELECT
				    ap.timePeriod,
				    t.productName,
				    CAST(ISNULL(ps.gross_revenue - ps.return_amount, 0) AS DECIMAL(10,2)) as revenue,
				    ISNULL(ps.total_units, 0) as unitsSold,
				    CAST(ap.total_revenue AS DECIMAL(10,2)) as periodTotalRevenue
				FROM AllPeriods ap
				CROSS JOIN Top5Products t
				LEFT JOIN ProductStats ps ON ps.timePeriod = ap.timePeriod
				    AND ps.productId = t.productId
				ORDER BY ap.timePeriod, revenue DESC
				""".formatted(
				// AllPeriods parameters
				timeFormat, Views.COL_ORDER_TOTALAMOUNT, Views.COL_RETURN_ORDER_FINAL_AMOUNT, Views.TBL_RETURN_ORDER,
				Views.TBL_ORDER, Views.COL_RETURN_ORDER_ORDER_ID, Views.COL_ORDER_ID, Views.COL_RETURN_ORDER_STATUS,
				timeFormat, timeFormat, Views.TBL_ORDER, Views.COL_ORDER_STATUS, periodCondition, timeFormat,

				// OrderRevenue parameters
				timeFormat, Views.COL_PRODUCT_ID, Views.COL_PRODUCT_NAME, Views.COL_ORDER_DETAIL_PRICE,
				Views.COL_ORDER_DETAIL_QUANTITY, Views.COL_ORDER_DETAIL_QUANTITY, Views.TBL_ORDER,
				Views.TBL_ORDER_DETAIL, Views.COL_ORDER_ID, Views.COL_ORDER_DETAIL_ORDER_ID, Views.TBL_PRODUCT,
				Views.COL_ORDER_DETAIL_PRODUCT_ID, Views.COL_PRODUCT_ID, Views.COL_ORDER_STATUS, periodCondition,
				timeFormat, Views.COL_PRODUCT_ID, Views.COL_PRODUCT_NAME,

				// ReturnRevenue parameters
				timeFormat, Views.COL_ORDER_DETAIL_PRODUCT_ID, Views.COL_RETURN_DETAIL_AMOUNT,
				Views.COL_RETURN_DETAIL_QUANTITY, Views.TBL_ORDER, Views.TBL_RETURN_ORDER,
				Views.COL_RETURN_ORDER_ORDER_ID, Views.COL_ORDER_ID, Views.TBL_RETURN_ORDER_DETAIL,
				Views.COL_RETURN_DETAIL_RETURN_ID, Views.COL_RETURN_ORDER_ID, Views.TBL_ORDER_DETAIL,
				Views.COL_RETURN_DETAIL_ORDER_DETAIL_ID, Views.COL_ORDER_DETAIL_ID, Views.COL_RETURN_ORDER_STATUS,
				timeFormat, Views.COL_ORDER_DETAIL_PRODUCT_ID);

		return db.queryForList(sql);
	}

	public List<Map<String, Object>> getProductSummaryTrend(String period) {
		String timeFormat = switch (period) {
		case "monthly" -> "FORMAT(DATEFROMPARTS(YEAR(o.[" + Views.COL_ORDER_DATE + "]), MONTH(o.["
				+ Views.COL_ORDER_DATE + "]), 1), 'yyyy-MM')";
		case "quarterly" -> "CAST(YEAR(o.[" + Views.COL_ORDER_DATE
				+ "]) AS VARCHAR) + '-Q' + CAST(DATEPART(QUARTER, o.[" + Views.COL_ORDER_DATE + "]) AS VARCHAR)";
		case "yearly" -> "CAST(YEAR(o.[" + Views.COL_ORDER_DATE + "]) AS VARCHAR)";
		default -> throw new IllegalArgumentException("Invalid period: " + period);
		};

		String groupBy = switch (period) {
		case "monthly" -> "YEAR(o.[" + Views.COL_ORDER_DATE + "]), MONTH(o.[" + Views.COL_ORDER_DATE + "])";
		case "quarterly" ->
			"YEAR(o.[" + Views.COL_ORDER_DATE + "]), DATEPART(QUARTER, o.[" + Views.COL_ORDER_DATE + "])";
		case "yearly" -> "YEAR(o.[" + Views.COL_ORDER_DATE + "])";
		default -> throw new IllegalArgumentException("Invalid period: " + period);
		};

		String periodCondition = getPeriodCondition(period);

		String sql = """
				WITH OrderRevenue AS (
				    SELECT
				        %s as month,
				        o.Id as OrderId,
				        o.totalAmount as revenue,
				        COUNT(od.Id) as quantity
				    FROM [%s] o
				    JOIN [%s] od ON o.[%s] = od.[%s]
				    WHERE o.[%s] = 'Completed'
				        AND %s
				    GROUP BY %s, o.Id, o.totalAmount
				),
				ReturnRevenue AS (
				    SELECT
				        %s as month,
				        ro.Order_Id,
				        ro.Final_Amount as return_amount,
				        COUNT(rod.Id) as return_quantity
				    FROM [%s] o
				    JOIN [%s] ro ON ro.[%s] = o.[%s]
				    JOIN [%s] rod ON rod.[%s] = ro.[%s]
				    WHERE ro.Status IN ('Completed', 'Accepted')
				    GROUP BY %s, ro.Order_Id, ro.Final_Amount
				),
				ProductStats AS (
				    SELECT
				        o.month as timePeriod,
				        SUM(o.revenue) as grossRevenue,
				        ISNULL(SUM(r.return_amount), 0) as returnAmount,
				        SUM(o.quantity) - ISNULL(SUM(r.return_quantity), 0) as totalUnitsSold
				    FROM OrderRevenue o
				    LEFT JOIN ReturnRevenue r ON r.month = o.month
				        AND r.Order_Id = o.OrderId
				    GROUP BY o.month
				)
				SELECT
				    p.month as timePeriod,
				    ISNULL(ps.totalUnitsSold, 0) as totalUnitsSold,
				    CAST(ISNULL(ps.grossRevenue - ps.returnAmount, 0) /
				        NULLIF(ps.totalUnitsSold, 0) AS DECIMAL(10,2)) as averagePrice,
				    CAST(ISNULL(ps.returnAmount * 100.0 /
				        NULLIF(ps.grossRevenue, 0), 0) AS DECIMAL(10,2)) as returnRate,
				    CAST(ISNULL(ps.grossRevenue - ps.returnAmount, 0) AS DECIMAL(10,2)) as revenue,
				    CAST(ISNULL(ps.grossRevenue, 0) AS DECIMAL(10,2)) as debug_grossRevenue,
				    CAST(ISNULL(ps.returnAmount, 0) AS DECIMAL(10,2)) as debug_returnAmount
				FROM (
				    SELECT DISTINCT %s as month
				    FROM [%s] o
				    WHERE o.[%s] = 'Completed' AND %s
				) p
				LEFT JOIN ProductStats ps ON p.month = ps.timePeriod
				ORDER BY p.month
				""".formatted(
				// OrderRevenue parameters
				timeFormat, Views.TBL_ORDER, Views.TBL_ORDER_DETAIL, Views.COL_ORDER_ID,
				Views.COL_ORDER_DETAIL_ORDER_ID, Views.COL_ORDER_STATUS, periodCondition, timeFormat + ", " + groupBy,

				// ReturnRevenue parameters
				timeFormat, Views.TBL_ORDER, Views.TBL_RETURN_ORDER, Views.COL_RETURN_ORDER_ORDER_ID,
				Views.COL_ORDER_ID, Views.TBL_RETURN_ORDER_DETAIL, Views.COL_RETURN_DETAIL_RETURN_ID,
				Views.COL_RETURN_ORDER_ID, timeFormat + ", " + groupBy,

				// Final SELECT parameters
				timeFormat, Views.TBL_ORDER, Views.COL_ORDER_STATUS, periodCondition);

		return db.queryForList(sql);
	}

	public List<Map<String, Object>> getWarehouseRevenue(String period) {
		String timeFormat = switch (period) {
		case "monthly" -> "FORMAT(DATEFROMPARTS(YEAR(o.[" + Views.COL_ORDER_DATE + "]), MONTH(o.["
				+ Views.COL_ORDER_DATE + "]), 1), 'yyyy-MM')";
		case "quarterly" -> "CAST(YEAR(o.[" + Views.COL_ORDER_DATE
				+ "]) AS VARCHAR) + '-Q' + CAST(DATEPART(QUARTER, o.[" + Views.COL_ORDER_DATE + "]) AS VARCHAR)";
		case "yearly" -> "CAST(YEAR(o.[" + Views.COL_ORDER_DATE + "]) AS VARCHAR)";
		default -> throw new IllegalArgumentException("Invalid period: " + period);
		};

		String groupBy = switch (period) {
		case "monthly" -> "YEAR(o.[" + Views.COL_ORDER_DATE + "]), MONTH(o.[" + Views.COL_ORDER_DATE + "])";
		case "quarterly" ->
			"YEAR(o.[" + Views.COL_ORDER_DATE + "]), DATEPART(QUARTER, o.[" + Views.COL_ORDER_DATE + "])";
		case "yearly" -> "YEAR(o.[" + Views.COL_ORDER_DATE + "])";
		default -> throw new IllegalArgumentException("Invalid period: " + period);
		};

		String periodCondition = getPeriodCondition(period);

		String sql = """
				WITH OrderRevenue AS (
				    SELECT
				        %s as month,
				        w.Id as warehouseId,
				        w.Name as warehouseName,
				        o.Id as OrderId,
				        o.totalAmount as revenue,
				        COUNT(od.Id) as totalUnits
				    FROM [Warehouse] w
				    JOIN [%s] o ON w.Id = o.[%s]
				    JOIN [%s] od ON o.[%s] = od.[%s]
				    WHERE o.[%s] = 'Completed'
				        AND %s
				    GROUP BY %s, w.Id, w.Name, o.Id, o.totalAmount
				),
				ReturnRevenue AS (
				    SELECT
				        %s as month,
				        w.Id as warehouseId,
				        ro.Order_Id,
				        ro.Final_Amount as return_amount
				    FROM [Warehouse] w
				    JOIN [%s] o ON w.Id = o.[%s]
				    JOIN [%s] ro ON ro.[%s] = o.[%s]
				    WHERE ro.Status = 'Accepted'
				    GROUP BY %s, w.Id, ro.Order_Id, ro.Final_Amount
				),
				WarehouseStats AS (
				    SELECT
				        o.month,
				        o.warehouseId,
				        o.warehouseName,
				        SUM(o.revenue) as gross_revenue,
				        ISNULL(SUM(r.return_amount), 0) as return_amount,
				        COUNT(DISTINCT o.OrderId) as totalOrders,
				        SUM(o.totalUnits) as totalUnits
				    FROM OrderRevenue o
				    LEFT JOIN ReturnRevenue r ON r.month = o.month
				        AND r.warehouseId = o.warehouseId
				        AND r.Order_Id = o.OrderId
				    GROUP BY o.month, o.warehouseId, o.warehouseName
				),
				TotalRevenue AS (
				    SELECT
				        %s as month,
				        SUM(o.totalAmount) as gross_revenue,
				        COALESCE(SUM(CASE
				            WHEN ro.Status = 'Accepted'
				            THEN ro.Final_Amount
				            ELSE 0
				        END), 0) as return_amount
				    FROM [%s] o
				    LEFT JOIN [%s] ro ON ro.[%s] = o.[%s]
				    WHERE o.[%s] = 'Completed'
				        AND %s
				    GROUP BY %s
				)
				SELECT
				    ws.month as timePeriod,
				    ws.warehouseName,
				    ws.warehouseId,
				    ws.gross_revenue - ws.return_amount as NetRevenue,
				    ws.totalOrders,
				    ws.totalUnits,
				    CAST((ws.gross_revenue - ws.return_amount) * 100.0 /
				        NULLIF(t.gross_revenue - t.return_amount, 0) as DECIMAL(10,2)) as percentage
				FROM WarehouseStats ws
				JOIN TotalRevenue t ON t.month = ws.month
				ORDER BY ws.month, (ws.gross_revenue - ws.return_amount) DESC
				""".formatted(
				// OrderRevenue parameters
				timeFormat, Views.TBL_ORDER, Views.COL_ORDER_WAREHOUSE_ID, Views.TBL_ORDER_DETAIL, Views.COL_ORDER_ID,
				Views.COL_ORDER_DETAIL_ORDER_ID, Views.COL_ORDER_STATUS, periodCondition, timeFormat + ", " + groupBy,

				// ReturnRevenue parameters
				timeFormat, Views.TBL_ORDER, Views.COL_ORDER_WAREHOUSE_ID, Views.TBL_RETURN_ORDER,
				Views.COL_RETURN_ORDER_ORDER_ID, Views.COL_ORDER_ID, timeFormat + ", " + groupBy,

				// TotalRevenue parameters
				timeFormat, Views.TBL_ORDER, Views.TBL_RETURN_ORDER, Views.COL_RETURN_ORDER_ORDER_ID,
				Views.COL_ORDER_ID, Views.COL_ORDER_STATUS, periodCondition, timeFormat + ", " + groupBy);

		return db.queryForList(sql);
	}

	public Map<String, Object> getProvinceRevenue(String period) {
		String timeFormat = switch (period) {
		case "monthly" -> "FORMAT(DATEFROMPARTS(YEAR(o.[" + Views.COL_ORDER_DATE + "]), MONTH(o.["
				+ Views.COL_ORDER_DATE + "]), 1), 'yyyy-MM')";
		case "quarterly" -> "CAST(YEAR(o.[" + Views.COL_ORDER_DATE
				+ "]) AS VARCHAR) + '-Q' + CAST(DATEPART(QUARTER, o.[" + Views.COL_ORDER_DATE + "]) AS VARCHAR)";
		case "yearly" -> "CAST(YEAR(o.[" + Views.COL_ORDER_DATE + "]) AS VARCHAR)";
		default -> throw new IllegalArgumentException("Invalid period: " + period);
		};

		String periodCondition = getPeriodCondition(period);

		String sql = """
				WITH CompletedOrders AS (
				    -- Lấy các đơn hàng completed và group theo period và province
				    SELECT
				        %s as timePeriod,
				        o.[%s] as provinceId,
				        SUM(CAST(o.[%s] as DECIMAL(10,2))) as totalAmount,
				        COUNT(DISTINCT o.[%s]) as totalOrders,
				        COUNT(DISTINCT o.[%s]) as uniqueCustomers
				    FROM [%s] o
				    WHERE o.[%s] = 'Completed'
				        AND o.[%s] IS NOT NULL
				        AND %s
				    GROUP BY %s, o.[%s]
				),
				ReturnAmount AS (
				    -- Lấy tổng return amount theo period và province
				    SELECT
				        %s as timePeriod,
				        o.[%s] as provinceId,
				        SUM(CAST(ro.[%s] as DECIMAL(10,2))) as returnAmount
				    FROM [%s] ro
				    JOIN [%s] o ON o.[%s] = ro.[%s]
				    WHERE ro.[%s] IN ('Completed', 'Accepted')
				        AND %s
				    GROUP BY %s, o.[%s]
				),
				ProvinceStats AS (
				    -- Tính toán revenue và percentage
				    SELECT
				        co.timePeriod,
				        co.provinceId,
				        co.totalOrders,
				        co.uniqueCustomers,
				        co.totalAmount as grossRevenue,
				        ISNULL(ra.returnAmount, 0) as returnAmount,
				        co.totalAmount - ISNULL(ra.returnAmount, 0) as netRevenue
				    FROM CompletedOrders co
				    LEFT JOIN ReturnAmount ra ON ra.provinceId = co.provinceId
				        AND ra.timePeriod = co.timePeriod
				),
				LatestPeriod AS (
				    -- Lấy period mới nhất
				    SELECT MAX(timePeriod) as latestPeriod
				    FROM ProvinceStats
				)
				SELECT
				    ps.*,
				    CAST(CASE
				        WHEN SUM(ps.netRevenue) OVER (PARTITION BY ps.timePeriod) = 0 THEN 0
				        ELSE ps.netRevenue * 100.0 /
				             SUM(ps.netRevenue) OVER (PARTITION BY ps.timePeriod)
				    END AS DECIMAL(10,2)) as percentage,
				    CASE WHEN ps.timePeriod = lp.latestPeriod THEN 1 ELSE 0 END as isLatest
				FROM ProvinceStats ps
				CROSS JOIN LatestPeriod lp
				ORDER BY ps.timePeriod DESC, ps.netRevenue DESC
				""".formatted(timeFormat, Views.COL_ORDER_PROVINCE_ID, Views.COL_ORDER_TOTALAMOUNT, Views.COL_ORDER_ID,
				Views.COL_ORDER_CUSTOMER_ID, Views.TBL_ORDER, Views.COL_ORDER_STATUS, Views.COL_ORDER_PROVINCE_ID,
				periodCondition, timeFormat, Views.COL_ORDER_PROVINCE_ID,

				timeFormat, Views.COL_ORDER_PROVINCE_ID, Views.COL_RETURN_ORDER_FINAL_AMOUNT, Views.TBL_RETURN_ORDER,
				Views.TBL_ORDER, Views.COL_ORDER_ID, Views.COL_RETURN_ORDER_ORDER_ID, Views.COL_RETURN_ORDER_STATUS,
				periodCondition, timeFormat, Views.COL_ORDER_PROVINCE_ID);

		List<Map<String, Object>> results = db.queryForList(sql);
		Map<String, Object> response = new HashMap<>();

		// Tách dữ liệu thành 2 phần
		List<Map<String, Object>> mapData = results.stream().filter(r -> ((Integer) r.get("isLatest")) == 1)
				.collect(Collectors.toList());

		Map<Object, List<Map<String, Object>>> historicalData = results.stream()
				.collect(Collectors.groupingBy(r -> r.get("provinceId")));

		response.put("mapData", mapData);
		response.put("historicalData", historicalData);

		return response;
	}

	public List<Map<String, Object>> getGrossProfitAnalysis(String period) {
		String timeFormat = switch (period) {
		case "monthly" -> "FORMAT(o.[" + Views.COL_ORDER_DATE + "], 'yyyy-MM')";
		case "quarterly" -> "CAST(YEAR(o.[" + Views.COL_ORDER_DATE
				+ "]) AS VARCHAR) + '-Q' + CAST(DATEPART(QUARTER, o.[" + Views.COL_ORDER_DATE + "]) AS VARCHAR)";
		case "yearly" -> "CAST(YEAR(o.[" + Views.COL_ORDER_DATE + "]) AS VARCHAR)";
		default -> throw new IllegalArgumentException("Invalid period: " + period);
		};

		String groupBy = switch (period) {
		case "monthly" -> "YEAR(o.[" + Views.COL_ORDER_DATE + "]), MONTH(o.[" + Views.COL_ORDER_DATE + "])";
		case "quarterly" ->
			"YEAR(o.[" + Views.COL_ORDER_DATE + "]), DATEPART(QUARTER, o.[" + Views.COL_ORDER_DATE + "])";
		case "yearly" -> "YEAR(o.[" + Views.COL_ORDER_DATE + "])";
		default -> throw new IllegalArgumentException("Invalid period: " + period);
		};

		String periodCondition = getPeriodCondition(period);

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
				        %s as timePeriod,
				        od.%s as productId,
				        p.%s as productName,
				        SUM(od.%s) as soldQuantity,
				        SUM(od.%s * od.%s) as revenue
				    FROM [%s] o
				    JOIN [%s] od ON o.%s = od.%s
				    JOIN [%s] p ON od.%s = p.%s
				    WHERE o.%s = 'Completed'
				        AND %s
				    GROUP BY
				        %s,
				        od.%s,
				        p.%s
				),
				ReturnData AS (
				    SELECT
				        %s as timePeriod,
				        od.%s as productId,
				        SUM(rod.%s) as returnQuantity,
				        SUM(rod.%s) as returnAmount
				    FROM [%s] ro
				    JOIN [%s] rod ON ro.%s = rod.%s
				    JOIN [%s] o ON ro.%s = o.%s
				    JOIN [%s] od ON rod.%s = od.%s
				    WHERE ro.%s IN ('Accepted', 'Completed')
				    GROUP BY
				        %s,
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
				Views.TBL_WAREHOUSE_RECEIPT_DETAIL, Views.TBL_WAREHOUSE_RECEIPT, Views.COL_DETAIL_WAREHOUSE_RECEIPT_ID,
				Views.COL_WAREHOUSE_RECEIPT_ID, Views.COL_WAREHOUSE_RECEIPT_DETAILS_STATUS,

				// OrderData CTE
				timeFormat, Views.COL_ORDER_DETAIL_PRODUCT_ID, Views.COL_PRODUCT_NAME, Views.COL_ORDER_DETAIL_QUANTITY,
				Views.COL_ORDER_DETAIL_QUANTITY, Views.COL_ORDER_DETAIL_PRICE, Views.TBL_ORDER, Views.TBL_ORDER_DETAIL,
				Views.COL_ORDER_ID, Views.COL_ORDER_DETAIL_ORDER_ID, Views.TBL_PRODUCT,
				Views.COL_ORDER_DETAIL_PRODUCT_ID, Views.COL_PRODUCT_ID, Views.COL_ORDER_STATUS, periodCondition,
				timeFormat + ", " + groupBy, Views.COL_ORDER_DETAIL_PRODUCT_ID, Views.COL_PRODUCT_NAME,

				// ReturnData CTE
				timeFormat, Views.COL_ORDER_DETAIL_PRODUCT_ID, Views.COL_RETURN_DETAIL_QUANTITY,
				Views.COL_RETURN_DETAIL_AMOUNT, Views.TBL_RETURN_ORDER, Views.TBL_RETURN_ORDER_DETAIL,
				Views.COL_RETURN_ORDER_ID, Views.COL_RETURN_DETAIL_RETURN_ID, Views.TBL_ORDER,
				Views.COL_RETURN_ORDER_ORDER_ID, Views.COL_ORDER_ID, Views.TBL_ORDER_DETAIL,
				Views.COL_RETURN_DETAIL_ORDER_DETAIL_ID, Views.COL_ORDER_DETAIL_ID, Views.COL_RETURN_ORDER_STATUS,
				timeFormat + ", " + groupBy, Views.COL_ORDER_DETAIL_PRODUCT_ID);

		return db.queryForList(sql);
	}

	public List<Map<String, Object>> getPredictedRevenue(List<Map<String, Object>> currentData, String period) {
	    // Lấy data đến tháng 12 để hiển thị
	    YearMonth currentMonth = YearMonth.now();
	    String currentMonthStr = currentMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
	    
	    List<Map<String, Object>> result = new ArrayList<>(currentData);
	    
	    // Lọc dữ liệu đến tháng 11 để dự đoán
	    YearMonth endMonth = currentMonth.minusMonths(1);  // tháng 11
	    String endMonthStr = endMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
	    
	    List<Map<String, Object>> filteredData = currentData.stream()
	        .filter(row -> row.get("timePeriod").toString().compareTo(endMonthStr) <= 0)
	        .collect(Collectors.toList());
	    
	    List<Double> revenues = filteredData.stream()
	        .map(row -> ((Number)row.get("TotalRevenue")).doubleValue())
	        .collect(Collectors.toList());
	            
	    List<String> periods = filteredData.stream()
	        .map(row -> row.get("timePeriod").toString())
	        .collect(Collectors.toList());

	    // Dự đoán cho tháng 1 và 2
	    YearMonth nextMonth = currentMonth.plusMonths(1); 
	    
	    for (int i = 1; i <= 2; i++) {
	        String nextPeriod = nextMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
	        double predictedValue = calculatePrediction(revenues, period);
	        
	        Map<String, Object> prediction = new HashMap<>();
	        prediction.put("timePeriod", nextPeriod);
	        prediction.put("TotalRevenue", predictedValue);
	        prediction.put("SelectedRevenue", 0.0);
	        prediction.put("SelectedPercentage", 0.0);
	        prediction.put("isPredicted", true);
	        
	        result.add(prediction);
	        
	        revenues.add(predictedValue);
	        periods.add(nextPeriod);
	        nextMonth = nextMonth.plusMonths(1);
	    }
	    
	    return result;
	}

	private double calculatePrediction(List<Double> revenues, String periodType) {
	    if (revenues.size() < 3) return 0;
	    
	    
	    int size = revenues.size();
	    double recentAvg = 0;
	    for (int i = size - 3; i < size; i++) {
	        recentAvg += revenues.get(i);
	    }
	    recentAvg = recentAvg / 3;

	    
	    double lastValue = revenues.get(size - 1);
	    double previousValue = revenues.get(size - 2);
	    double growthRate = (lastValue - previousValue) / previousValue;
	    
	    growthRate = Math.max(-0.2, Math.min(0.2, growthRate));

	    
	    double prediction = lastValue * (1 + growthRate) * 0.6 + recentAvg * 0.4;

	  
	    double maxChange = 0.3;
	    double minValue = lastValue * (1 - maxChange);
	    double maxValue = lastValue * (1 + maxChange);
	    
	    return Math.max(minValue, Math.min(maxValue, prediction));
	}

	public List<Map<String, Object>> getProfitAnalysis(String period) {
	    // Định nghĩa CASE statement cho period format
	    String periodCase = switch (period) {
	        case "monthly" -> "FORMAT(%s, 'yyyy-MM')";
	        case "quarterly" -> "CAST(YEAR(%s) AS VARCHAR) + '-Q' + CAST(DATEPART(QUARTER, %s) AS VARCHAR)";
	        case "yearly" -> "CAST(YEAR(%s) AS VARCHAR)";
	        default -> throw new IllegalArgumentException("Invalid period: " + period);
	    };

	    // Tạo format cho từng loại date
	    String orderPeriodFormat = periodCase.formatted(
	        "o.[" + Views.COL_ORDER_DATE + "]",
	        "o.[" + Views.COL_ORDER_DATE + "]"
	    );
	    
	    String returnPeriodFormat = periodCase.formatted(
	        "ro.[" + Views.COL_RETURN_ORDER_DATE + "]",
	        "ro.[" + Views.COL_RETURN_ORDER_DATE + "]"
	    );
	    
	    String expensePeriodFormat = periodCase.formatted(
	        "eh.[" + Views.COL_EXPENSE_HISTORY_START_DATE + "]",
	        "eh.[" + Views.COL_EXPENSE_HISTORY_START_DATE + "]"
	    );
	    
	    String vatPeriodFormat = periodCase.formatted(
	        "th.[" + Views.COL_TAX_HISTORY_PERIOD_START + "]",
	        "th.[" + Views.COL_TAX_HISTORY_PERIOD_START + "]"
	    );

	    String sql = """
	            WITH ProductCostPrice AS (
	                SELECT DISTINCT
	                    wrd.%s as productId,
	                    FIRST_VALUE(wrd.%s) OVER (
	                        PARTITION BY wrd.%s
	                        ORDER BY wr.%s DESC, wrd.%s DESC
	                    ) as latestCostPrice
	                FROM [%s] wrd
	                JOIN [%s] wr ON wrd.%s = wr.%s
	                WHERE wrd.%s = 'Active'
	            ),
	            OrderData AS (
	                SELECT 
	                    %s as period,
	                    SUM(od.%s * od.%s) as revenue,
	                    SUM(od.%s * pc.latestCostPrice) as costAmount
	                FROM [%s] o
	                JOIN [%s] od ON o.%s = od.%s
	                LEFT JOIN ProductCostPrice pc ON od.%s = pc.productId
	                WHERE o.%s = 'Completed'
	                GROUP BY %s
	            ),
	            ReturnData AS (
	                SELECT 
	                    %s as period,
	                    SUM(rod.%s) as returnAmount,
	                    SUM(rod.%s * pc.latestCostPrice) as returnCost
	                FROM [%s] ro
	                JOIN [%s] rod ON ro.%s = rod.%s
	                JOIN [%s] od ON rod.%s = od.%s
	                LEFT JOIN ProductCostPrice pc ON od.%s = pc.productId
	                WHERE ro.%s IN ('Completed', 'Accepted')
	                GROUP BY %s
	            ),
	            ExpenseData AS (
	                SELECT 
	                    %s as period,
	                    CAST(SUM(
	                        CASE 
	                            WHEN et.%s = 'true' THEN eh.%s
	                            ELSE eh.%s / NULLIF(DATEDIFF(MONTH, eh.%s, eh.%s) + 1, 0)
	                        END
	                    ) AS DECIMAL(10,2)) as total_expense
	                FROM [%s] eh
	                JOIN [%s] et ON eh.%s = et.%s
	                WHERE eh.%s <= eh.%s
	                GROUP BY %s
	            ),
	            VATData AS (
	                SELECT 
	                    %s as period,
	                    CAST(SUM(th.%s) AS DECIMAL(10,2)) as vat_amount
	                FROM [%s] th
	                WHERE th.%s = 'VAT'
	                AND th.%s = 'Paid'
	                GROUP BY %s
	            )
	            SELECT 
	                COALESCE(o.period, r.period, e.period, v.period) as period,
	                CAST(COALESCE(o.revenue, 0) - COALESCE(r.returnAmount, 0) AS DECIMAL(10,2)) as Revenue,
	                CAST(COALESCE(o.revenue - o.costAmount, 0) - COALESCE(r.returnAmount - r.returnCost, 0) AS DECIMAL(10,2)) as GrossProfit,
	                COALESCE(e.total_expense, 0) as Expenses,
	                COALESCE(v.vat_amount, 0) as VAT,
	                CAST(
	                    (COALESCE(o.revenue - o.costAmount, 0) - COALESCE(r.returnAmount - r.returnCost, 0)) - 
	                    COALESCE(e.total_expense, 0) - COALESCE(v.vat_amount, 0) 
	                AS DECIMAL(10,2)) as Net_Profit
	            FROM OrderData o
	            FULL OUTER JOIN ReturnData r ON r.period = o.period
	            FULL OUTER JOIN ExpenseData e ON e.period = o.period
	            FULL OUTER JOIN VATData v ON v.period = o.period
	            ORDER BY period DESC
	            """.formatted(
	                // ProductCostPrice
	                Views.COL_WAREHOUSE_RECEIPT_DETAIL_PRODUCT_ID,
	                Views.COL_WAREHOUSE_RECEIPT_DETAIL_WH_PRICE,
	                Views.COL_WAREHOUSE_RECEIPT_DETAIL_PRODUCT_ID,
	                Views.COL_WAREHOUSE_RECEIPT_DATE,
	                Views.COL_DETAIL_WAREHOUSE_RECEIPT_ID,
	                Views.TBL_WAREHOUSE_RECEIPT_DETAIL,
	                Views.TBL_WAREHOUSE_RECEIPT,
	                Views.COL_DETAIL_WAREHOUSE_RECEIPT_ID,
	                Views.COL_WAREHOUSE_RECEIPT_ID,
	                Views.COL_WAREHOUSE_RECEIPT_DETAILS_STATUS,

	                // OrderData
	                orderPeriodFormat,
	                Views.COL_ORDER_DETAIL_QUANTITY,
	                Views.COL_ORDER_DETAIL_PRICE,
	                Views.COL_ORDER_DETAIL_QUANTITY,
	                Views.TBL_ORDER,
	                Views.TBL_ORDER_DETAIL,
	                Views.COL_ORDER_ID,
	                Views.COL_ORDER_DETAIL_ORDER_ID,
	                Views.COL_ORDER_DETAIL_PRODUCT_ID,
	                Views.COL_ORDER_STATUS,
	                orderPeriodFormat,

	                // ReturnData
	                returnPeriodFormat,
	                Views.COL_RETURN_DETAIL_AMOUNT,
	                Views.COL_RETURN_DETAIL_QUANTITY,
	                Views.TBL_RETURN_ORDER,
	                Views.TBL_RETURN_ORDER_DETAIL,
	                Views.COL_RETURN_ORDER_ID,
	                Views.COL_RETURN_DETAIL_RETURN_ID,
	                Views.TBL_ORDER_DETAIL,
	                Views.COL_RETURN_DETAIL_ORDER_DETAIL_ID,
	                Views.COL_ORDER_DETAIL_ID,
	                Views.COL_ORDER_DETAIL_PRODUCT_ID,
	                Views.COL_RETURN_ORDER_STATUS,
	                returnPeriodFormat,

	                // ExpenseData
	                expensePeriodFormat,
	                Views.COL_EXPENSE_TYPE_IS_FIXED,
	                Views.COL_EXPENSE_HISTORY_AMOUNT,
	                Views.COL_EXPENSE_HISTORY_AMOUNT,
	                Views.COL_EXPENSE_HISTORY_START_DATE,
	                Views.COL_EXPENSE_HISTORY_END_DATE,
	                Views.TBL_EXPENSE_HISTORY,
	                Views.TBL_EXPENSE_TYPES,
	                Views.COL_EXPENSE_HISTORY_TYPE_ID,
	                Views.COL_EXPENSE_TYPE_ID,
	                Views.COL_EXPENSE_HISTORY_START_DATE,
	                Views.COL_EXPENSE_HISTORY_END_DATE,
	                expensePeriodFormat,

	                // VATData
	                vatPeriodFormat,
	                Views.COL_TAX_HISTORY_AMOUNT,
	                Views.TBL_TAX_HISTORY,
	                Views.COL_TAX_HISTORY_TYPE,
	                Views.COL_TAX_HISTORY_STATUS,
	                vatPeriodFormat
	            );

	        // In kết quả
	        List<Map<String, Object>> results = db.queryForList(sql);
	        printResults(results, period);
	        return results;
	    }

	    private void printResults(List<Map<String, Object>> results, String period) {
	        System.out.println("\nPhân tích lợi nhuận theo " + period + ":");
	        System.out.println("+-----------------+----------------+----------------+----------------+----------------+----------------+");
	        System.out.println("|      Period     |     Revenue    | Gross Profit  |    Expenses    |      VAT       |  Net Profit   |");
	        System.out.println("+-----------------+----------------+----------------+----------------+----------------+----------------+");

	        for (Map<String, Object> row : results) {
	            System.out.printf("| %-15s | %,14.2f | %,14.2f | %,14.2f | %,14.2f | %,14.2f |%n",
	                row.get("period"),
	                row.get("Revenue"),
	                row.get("GrossProfit"),
	                row.get("Expenses"),
	                row.get("VAT"),
	                row.get("Net_Profit")
	            );
	        }
	        System.out.println("+-----------------+----------------+----------------+----------------+----------------+----------------+");
	    }

}
	
	

