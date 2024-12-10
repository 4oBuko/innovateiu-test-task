package org.example;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import org.example.DocumentManager.Document;
import org.example.DocumentManager.SearchRequest;
import org.example.DocumentManager.Author;


class DocumentManagerTest {

    private final DocumentManager documentManager = new DocumentManager();

    private final static List<Author> authors = new LinkedList<>();

    static {
        authors.add(
                Author.builder()
                        .id("1")
                        .name("Haruki Murakami")
                        .build()
        );
        authors.add(
                Author.builder()
                        .id("2")
                        .name("George Orwell")
                        .build()
        );
        authors.add(
                Author.builder()
                        .id("3")
                        .name("Jane Austen")
                        .build()
        );
        authors.add(
                Author.builder()
                        .id("4")
                        .name("Mark Twain")
                        .build()
        );
        authors.add(
                Author.builder()
                        .id("5")
                        .name("Agatha Christie")
                        .build()
        );

    }

    @BeforeEach
    void setUp() {
        documentManager.save(
                Document.builder()
                        .title("The Silent Voice")
                        .content("A compelling story of resilience.")
                        .author(authors.get(0))
                        .created(Instant.now())
                        .id("1")
                        .build()
        );
        documentManager.save(
                Document.builder()
                        .title("Winds of Change")
                        .content("Exploring the depths of human emotion.")
                        .author(authors.get(1))
                        .created(Instant.now().plusSeconds(1000))
                        .id("2")
                        .build()
        );
        documentManager.save(
                Document.builder()
                        .title("Shadows and Light")
                        .content("An unforgettable tale of discovery.")
                        .author(authors.get(2))
                        .created(Instant.now().plusSeconds(100))
                        .id("3")
                        .build()
        );
        documentManager.save(
                Document.builder()
                        .title("The Echoes of the Past")
                        .content("A short and thrilling mystery.")
                        .author(authors.get(3))
                        .created(Instant.now().minusSeconds(1000))
                        .id("4")
                        .build()
        );
        documentManager.save(
                Document.builder()
                        .title("Journey Through Time")
                        .content("An adventure like no other.")
                        .author(authors.get(4))
                        .created(Instant.now().minusSeconds(100))
                        .id("5")
                        .build()
        );
    }

    @AfterEach
    void tearDown(){
        documentManager.clear();
    }

    @Test
    void saveWithoutId() {
        Document doc = documentManager.save(
                Document.builder()
                        .title("The Voice")
                        .content("A compelling story of resilience.")
                        .author(authors.get(1))
                        .created(Instant.now())
                        .build()
        );

        assertNotNull(doc.getId());
        assertFalse(doc.getId().isBlank());
    }

    @Test
    void saveWithId() {
        DocumentManager.Document doc = documentManager.save(
                Document.builder()
                        .title("Beyond the Horizon")
                        .content("A glimpse into the unknown future.")
                        .author(authors.get(1))
                        .created(Instant.now())
                        .id("6")
                        .build()
        );
        assertEquals("6", doc.getId());
    }

    @Test
    void saveWithExistingId() {
        Document doc = Document.builder()
                .title("JoJo's Bizarre Adventures: Steel Ball Run")
                .content("Go Johny! Go! Go!")
                .author(authors.get(4))
                .created(Instant.now().minusSeconds(100))
                .id("5")
                .build();
        Document d = documentManager.save(doc);
        assertNotEquals(d.getId(), "5");

    }

    @Test
    void search() {
        SearchRequest request = SearchRequest.builder()
                .authorIds(Arrays.asList("1", "2", "3"))
                .containsContents(Arrays.asList("An","story","human"))
                .titlePrefixes(Arrays.asList("of", "and"))
                .createdFrom(Instant.now())
                .createdTo(Instant.now().plusSeconds(5000))
                .build();
        List<Document> l = documentManager.search(request);
        assertEquals(l.size(), 2);
    }

    @Test
    void searchByTime() {
        SearchRequest request = SearchRequest.builder()
                .createdTo(Instant.now().minusSeconds(5))
                .createdFrom(Instant.now().minusSeconds(2000))
                .build();
        List<Document> d = documentManager.search(request);
        assertEquals(2, d.size());
        assertEquals("4", d.get(0).getId());
        assertEquals("5", d.get(1).getId());
    }

    @Test
    void searchByTittle(){
        SearchRequest request = SearchRequest.builder()
                .titlePrefixes(Arrays.asList("Silent", "Light", "Journey"))
                .build();
        List<Document> d = documentManager.search(request);
        assertEquals(3, d.size());
        assertEquals("1", d.get(0).getId());
        assertEquals("3",d.get(1).getId());
        assertEquals("5", d.get(2).getId());
    }

    @Test
    void searchAllFieldsEmpty() {
        SearchRequest request = SearchRequest.builder().build();
        List<Document> d = documentManager.search(request);
        assertEquals(5, d.size());
    }

    @Test
    void findById() {
        DocumentManager.Document doc = documentManager.findById("5").get();
        assertEquals("Journey Through Time", doc.getTitle());
    }

    @Test
    void findByIdObjectIsNotPresented() {
        assertThrows(NoSuchElementException.class, () -> documentManager.findById("6").get());
    }
}