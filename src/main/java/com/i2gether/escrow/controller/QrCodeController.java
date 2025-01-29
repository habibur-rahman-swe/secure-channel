package com.i2gether.escrow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.i2gether.escrow.service.QrCodeService;

@RestController
public class QrCodeController {

    @Autowired
    private QrCodeService qrCodeService;

    @GetMapping("/products/{id}/qr-code")
    public ResponseEntity<byte[]> getProductQrCode(@PathVariable Long id) {
        // Construct the URL for the product
        String productUrl = "http://localhost:8080/products/" + id;

        try {
            // Generate the QR code for the product URL
            byte[] qrCode = qrCodeService.generateQrCode(productUrl);

            // Set the response headers and return the image as a PNG
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "image/png");
            return new ResponseEntity<>(qrCode, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error generating QR code".getBytes());
        }
    }
}