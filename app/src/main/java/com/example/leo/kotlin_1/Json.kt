package com.example.leo.kotlin_1
import android.util.JsonReader
import android.util.JsonWriter
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.OutputStreamWriter

fun writeProduct(writer:JsonWriter, product:Product) {
    writer.beginObject() ;
    writer.name("id").value(product.id) ;
    writer.name("name").value(product.name) ;
    writer.endObject() ;
}

fun writeProductArray(writer: JsonWriter, products:List<Product>) {
    writer.beginArray() ;
    for(product in products) {
        writeProduct(writer, product) ;
    }
    writer.endArray();
}

fun writeJsonStream(out:OutputStream, products: List<Product>) {
    val writer = JsonWriter(OutputStreamWriter(out,"utf-8"))
    writer.setIndent("    ")
    writeProductArray(writer,products)
    writer.close();
}

fun readJsonStream(inputStream: InputStream) : List<Product> {
    val reader = JsonReader(InputStreamReader(inputStream,"UTF-8"));
    try {
        return  readProductArray(reader) ;
    }
    finally {
        reader.close()
    }
}

fun readProductArray(reader: JsonReader): List<Product> {
    val products = ArrayList<Product>() ;
    reader.beginArray() ;
    while (reader.hasNext()) {
        products.add(readProduct(reader)) ;
    }
    reader.endArray() ;
    return products ;
}

fun readProduct(reader: JsonReader): Product {
    var id = "" ;
    var name = "" ;
    reader.beginObject() ;
    while (reader.hasNext()) {
        var field = reader.nextName() ;
        if (field.equals("id")) {
            id = reader.nextString() ;
        }
        else if (field.equals("name")) {
            name = reader.nextString() ;
        }
        else {
            reader.skipValue() ;
        }
    }
    reader.endObject();
    return Product(id,name);
}
