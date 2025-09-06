package com.rollingstone.rag;


import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Component("localVectorStore")  // <- name it so it doesn't collide
public class VectorStore {
    record Item(String id, double[] embedding, String text, String source) implements Serializable {}

    private final Path path = Paths.get("data", "index.bin");
    private final List<Item> items = new ArrayList<>();

    public VectorStore() {
        try {
            Files.createDirectories(path.getParent());
            if (Files.exists(path)) load();
        } catch (IOException e){ throw new RuntimeException(e); }
    }

    public void upsert(String id, double[] emb, String text, String source){
        items.removeIf(i -> i.id.equals(id));
        items.add(new Item(id, emb, text, source));
        save();
    }


    public List<Map<String, Object>> topK(double[] query, int k) {
        return items.stream()
                .map(i -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", i.id);
                    m.put("source", i.source);
                    m.put("text", i.text);
                    m.put("score", cosine(query, i.embedding)); // auto-boxes to Double
                    return m;
                })
                .sorted(Comparator.comparingDouble(
                        (Map<String, Object> m) -> ((Number) m.get("score")).doubleValue()
                ).reversed())
                .limit(k)
                .collect(Collectors.toList());
    }

    private static double cosine(double[] a, double[] b){
        double dot=0,n1=0,n2=0;
        for(int i=0;i<a.length;i++){ dot+=a[i]*b[i]; n1+=a[i]*a[i]; n2+=b[i]*b[i]; }
        return dot/(Math.sqrt(n1)*Math.sqrt(n2)+1e-9);
    }

    private void save(){
        try(ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(path))){
            oos.writeObject(new ArrayList<>(items));
        } catch(IOException e){ throw new RuntimeException(e); }
    }

    @SuppressWarnings("unchecked")
    private void load(){
        try(ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))){
            List<Item> loaded = (List<Item>) ois.readObject();
            items.addAll(loaded);
        } catch(Exception e){ throw new RuntimeException(e); }
    }
}

