package org.litvitnik;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
public class IdempotentController {
    HashMap<Long, HwOrderRow> orderRepository = new HashMap<>();
    private Long counter = 0L;

    @PostMapping("/orders")
    public ResponseEntity createOrder(@RequestBody HwOrder order, @RequestHeader("X-Request-Id") String idempotencyId) {
        Optional<Map.Entry<Long, HwOrderRow>> entry = orderRepository.entrySet().stream().filter(longHwOrderRowEntry ->
                longHwOrderRowEntry.getValue().getIdempotencyId().equals(idempotencyId)).findFirst();
        return entry.<ResponseEntity>map(longHwOrderRowEntry -> ResponseEntity
                .status(201)
                .body("{\"id\":\"" + longHwOrderRowEntry.getKey() + "\"}")).orElseGet(() -> ResponseEntity
                .status(201)
                .body("{\"id\":\"" + addNewOrder(order, idempotencyId) + "\"}"));
    }

    private Long addNewOrder(HwOrder order, String idempotencyId) {
        counter++;
        HwOrderRow hwOrderRow = HwOrderRow.builder()
                .hwOrder(order)
                .idempotencyId(idempotencyId)
                .build();
        orderRepository.put(counter, hwOrderRow);
        return counter;
    }
}
