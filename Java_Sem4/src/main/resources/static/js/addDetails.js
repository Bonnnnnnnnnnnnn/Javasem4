function filterProducts() {
       let input = document.getElementById('searchProduct');
       let filter = input.value.toLowerCase();
       
       let select = document.getElementById('product_id');
       let options = select.getElementsByTagName('option');
       
       for (let i = 1; i < options.length; i++) { 
           let option = options[i];
           let txtValue = option.textContent || option.innerText;
           if (txtValue.toLowerCase().indexOf(filter) > -1) {
               option.style.display = ""; 
           } else {
               option.style.display = "none"; 
           }
       }
   }