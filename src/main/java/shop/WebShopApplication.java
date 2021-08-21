package shop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
public class WebShopApplication implements CommandLineRunner {
    @Autowired
    private RestOperations restTemplate;

    public static void main(String[] args) {
        SpringApplication.run(WebShopApplication.class, args);
    }

    @Bean
    RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        return restTemplate;
    }

    @Override
    public void run(String... args) throws Exception {
        //create customer
        CustomerDTO customerDto = new CustomerDTO("101", "Frank", "Brown", "fBrown@Hotmail.com", "123456");
        AddressDTO addressDTO = new AddressDTO("1000 N main Street", "Fairfield", "52557", "USA");
        customerDto.setAddress(addressDTO);
        restTemplate.postForLocation("http://localhost:8092/customer", customerDto);
        // get customer
        System.out.println("\n-----Customer-------");
        CustomerDTO customerDtoGet = restTemplate.getForObject("http://localhost:8092/customer/101", CustomerDTO.class);
        System.out.println(customerDtoGet);

        //create products
        restTemplate.postForLocation("http://localhost:8091/product/A33/TV/250.00", null);
        restTemplate.postForLocation("http://localhost:8091/product/A34/MP3/75.00", null);
        //set stock		
        restTemplate.postForLocation("http://localhost:8091/product/stock/A33/433/A557", null);
        restTemplate.postForLocation("http://localhost:8091/product/stock/A34/250/A557", null);
        //get a product
        ProductDTO product = restTemplate.getForObject("http://localhost:8091/product/A33", ProductDTO.class);
        System.out.println("\n-----Product-------");
        product.print();
        // add products to the shoppingcart
        restTemplate.postForLocation("http://localhost:8093/cart/1/A33/3", null);
        restTemplate.postForLocation("http://localhost:8093/cart/1/A34/2", null);
        //get the shoppingcart
        ShoppingCartDTO cart = restTemplate.getForObject("http://localhost:8093/cart/1", ShoppingCartDTO.class);
        System.out.println("\n-----Shoppingcart-------");
        if (cart != null) cart.print();
        //checkout the cart
        restTemplate.postForLocation("http://localhost:8093/cart/checkout/1", null);
        //get the order
        OrderDTO order = restTemplate.getForObject("http://localhost:8094/order/1", OrderDTO.class);
        System.out.println("\n-----Order-------");
        if (order != null) order.print();

        //add customer to order
        restTemplate.postForLocation("http://localhost:8094/order/setCustomer/1/101", null);

        //confirm the order
        restTemplate.postForLocation("http://localhost:8094/order/1", null);

        //get the order
        OrderDTO orderconfirmed = restTemplate.getForObject("http://localhost:8094/order/1", OrderDTO.class);
        System.out.println("\n-----Confirmed Order-------");
        if (orderconfirmed != null) orderconfirmed.print();

    }


}
