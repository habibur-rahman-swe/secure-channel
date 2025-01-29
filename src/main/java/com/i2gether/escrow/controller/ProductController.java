package com.i2gether.escrow.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.i2gether.escrow.entity.Product;
import com.i2gether.escrow.service.ProductService;

@RestController
@RequestMapping("/products")
public class ProductController {

	@Autowired
	private ProductService productService;

	@GetMapping
	public List<Product> getAllProducts() {
		return productService.getAllProducts();
	}

	@PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
	public ResponseEntity<Product> createProduct(@RequestParam("name") String name,
			@RequestParam("description") String description, @RequestParam("quantity") Integer quantity,
			@RequestParam("image") MultipartFile imageFile) throws IOException {

		Product product = new Product();
		product.setName(name);
		product.setDescription(description);
		product.setQuantity(quantity);
		product.setImage(imageFile.getBytes());

		Product savedProduct = productService.saveProduct(product);

		// Add self-link to the product
		savedProduct.add(linkTo(methodOn(ProductController.class).getProductById(savedProduct.getId())).withSelfRel());

		return ResponseEntity.ok(savedProduct);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getProductById(@PathVariable Long id) {
		Optional<Product> optionalProduct = productService.getProductById(id);

		if (optionalProduct.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("message", "Product not found", "productId", id));
		}

		Product product = optionalProduct.get();
		product.add(linkTo(methodOn(ProductController.class).getProductById(id)).withSelfRel());

		return ResponseEntity.ok(product);
	}

	@GetMapping("/{id}/image")
	public ResponseEntity<byte[]> getProductImage(@PathVariable Long id) {
		Optional<Product> product = productService.getProductById(id);
		if (product.isPresent() && product.get().getImage() != null) {
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG) // Change based on the image type
					.header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + product.get().getName() + ".jpg\"")
					.body(product.get().getImage());
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}