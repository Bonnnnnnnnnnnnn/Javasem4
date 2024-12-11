document.addEventListener("DOMContentLoaded", function () {
    const chartElement = document.getElementById('lowStockChart');
    const apiUrlBase = "/warehouseManager/lowStockApi";
    let chart;

    // Hàm gọi API và cập nhật biểu đồ
    function updateChart(minQuantity) {
        const apiUrl = `${apiUrlBase}?minQuantity=${minQuantity || ''}`;

        fetch(apiUrl)
            .then(response => {
                if (!response.ok) {
                    throw new Error("API call failed: " + response.status);
                }
                return response.json();
            })
            .then(data => {
                console.log("Dữ liệu API trả về:", data);

                const labels = data.map(item => item.productName);
                const quantities = data.map(item => item.totalQuantity);
                const colors = data.map(item => item.totalQuantity < minQuantity ? 'rgba(255, 99, 132, 0.5)' : 'rgba(75, 192, 192, 0.5)');
                const borderColors = data.map(item => item.totalQuantity < minQuantity ? 'rgba(255, 99, 132, 1)' : 'rgba(75, 192, 192, 1)');

                if (chart) {
                    // Cập nhật dữ liệu cho biểu đồ hiện tại
                    chart.data.labels = labels;
                    chart.data.datasets[0].data = quantities;
                    chart.data.datasets[0].backgroundColor = colors;
                    chart.data.datasets[0].borderColor = borderColors;
                    chart.update();
                } else {
                    // Tạo biểu đồ mới nếu chưa có
                    chart = new Chart(chartElement, {
                        type: 'bar',
                        data: {
                            labels: labels,
                            datasets: [{
                                label: 'Quantity in stock',
                                data: quantities,
                                backgroundColor: colors,
                                borderColor: borderColors,
                                borderWidth: 1
                            }]
                        },
                        options: {
                            responsive: true,
                            plugins: {
                                legend: {
                                    position: 'top',
                                },
                                title: {
                                    display: true,
                                    text: 'Low Inventory Product Statistics'
                                }
                            },
                            scales: {
                                y: {
                                    beginAtZero: true,
                                    title: {
                                        display: true,
                                        text: 'Quantity'
                                    }
                                },
                                x: {
                                    title: {
                                        display: true,
                                        text: 'Product Name'
                                    }
                                }
                            }
                        }
                    });
                }
            })
            .catch(error => {
                console.error("Error loading data: ", error);
                alert("Không thể tải dữ liệu thống kê tồn kho!");
            });
    }

    // Gọi API lần đầu với minQuantity mặc định là rỗng (hiển thị tất cả)
    updateChart(null);

    // Lắng nghe sự kiện bấm nút cập nhật
    document.getElementById('updateChart').addEventListener('click', function () {
        const minQuantity = document.getElementById('minQuantity').value;
        updateChart(minQuantity);
    });
});
