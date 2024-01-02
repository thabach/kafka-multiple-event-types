package io.doubledispatch.kafka.multiple_event_types;

public class DataBuilder {
    
    public static io.doubledispatch.kafka.multiple_event_types.avro.PageView avroPageView() {
        return io.doubledispatch.kafka.multiple_event_types.avro.PageView.newBuilder()
                .setCustomerId("vandelay1234")
                .setIsSpecial(true)
                .setUrl("https://acme.commerce/sale")
                .build();
        
    }

    public static io.doubledispatch.kafka.multiple_event_types.avro.Purchase avroPurchase() {
        return io.doubledispatch.kafka.multiple_event_types.avro.Purchase.newBuilder()
                .setCustomerId("vandelay1234")
                .setAmount(437.83)
                .setItem("flux-capacitor")
                .build();
    }

    public static io.doubledispatch.kafka.multiple_event_types.json.PageView jsonSchemaPageView() {
        return new io.doubledispatch.kafka.multiple_event_types.json.PageView ()
                .withCustomerId("vandelay1234")
                .withIsSpecial(true)
                .withUrl("https://acme.commerce/sale");
    }

    public static io.doubledispatch.kafka.multiple_event_types.json.Purchase jsonSchemaPurchase() {
        return new io.doubledispatch.kafka.multiple_event_types.json.Purchase()
                .withAmount(437.83)
                .withCustomerId("vandelay1234")
                .withItem("flux-capacitor");
    }

}
