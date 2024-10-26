		//delete thông báo employee
		function confirmDeleteEmp() {
		   return confirm("Are you sure you want to delete this employee?");
		  }
		  //click vào xem chi tiết nhân viên
		  function goToDetailEmploy(id) {
		        window.location.href = '/admin/employee/showEmployeeDetail?id=' + id;
		    }
  