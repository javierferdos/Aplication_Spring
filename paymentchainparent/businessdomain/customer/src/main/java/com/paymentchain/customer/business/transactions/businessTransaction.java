/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.paymentchain.customer.business.transactions;

import com.fasterxml.jackson.databind.JsonNode;
import com.paymentchain.customer.entities.Customer;
import com.paymentchain.customer.entities.CustomerProduct;
import com.paymentchain.customer.exception.bussinesRuleException;
import com.paymentchain.customer.repository.CustomerRepository;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

/**
 *
 * @author javie
 */

@Service
public class businessTransaction {
    
    
      @Autowired
    private WebClient.Builder webClientBuilder;
      
      
      @Autowired
    CustomerRepository customerRepository;
    /*private final WebClient.Builder webClientBuilder;

    public CustomerRestController(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }*/
    

    HttpClient client = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .option(EpollChannelOption.TCP_KEEPIDLE, 300)
            .option(EpollChannelOption.TCP_KEEPINTVL, 60)
            .responseTimeout(Duration.ofSeconds(1))
            .doOnConnected(connection -> {
                connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
                connection.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
            });
    
    
   
    public Customer post(Customer input) throws bussinesRuleException {
        if(input.getProducts() != null){
            for(Iterator<CustomerProduct> it = input.getProducts().iterator(); it.hasNext();){
                CustomerProduct dto = it.next();
                String productName  = getProductName(dto.getProductId());
                if(productName.isBlank()){
                    bussinesRuleException bussinesRuleException = new bussinesRuleException ("1025", "Error de validacion, producto con id"+dto.getProductId()+"no existe", HttpStatus.PRECONDITION_FAILED);
                    throw bussinesRuleException;
                }else{
                    dto.setCustomer(input);
                }
            }
        }
        Customer save = customerRepository.save(input);
        return save;
    }
    
    
    
    public Customer getByCode(String code) {
    Customer customer = customerRepository.findByCode(code);
    if (customer == null) return null;

    List<CustomerProduct> products = customer.getProducts();
    products.forEach(x -> {
        String productName = getProductName(x.getProductId());
        x.setProductName(productName);
    });

    List<?> transactions = getTransactions(customer.getIban());
    customer.setTransactions(transactions);

    return customer;
}
    
    
    private String getProductName(Long id) {
    WebClient webClient = webClientBuilder
            .baseUrl("http://BUSINNESDOMAIN-PRODUCT/product")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    JsonNode block = webClient.get()
            .uri("/{id}", id)
            .retrieve()
            .bodyToMono(JsonNode.class)
            .block();

    return block != null && block.has("name") ? block.get("name").asText() : null;
}
    
    private List<?> getTransactions(String iban){
        
        WebClient build = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl("http://BUSINNESDOMAIN-TRANSACTIONS/transaction")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();
        
        List<?> transactions = build.method(HttpMethod.GET).uri(uriBuilder -> uriBuilder
                .path("/customer/transactions")
                .queryParam("ibanAccount", iban).build()).retrieve().bodyToFlux(Object.class).collectList().block();
    
        
        return transactions;
    }
    
}
