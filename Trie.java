import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Trie {
    private final TrieNode root;
    private final Map<String, Integer> wordCount; // Kelime sayısını tutan sembol tablosu

    public Trie() {
        root = new TrieNode();
        wordCount = new HashMap<>();
    }

    // Kelime ekleme
    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }
        node.isEndOfWord = true;
        wordCount.put(word, wordCount.getOrDefault(word, 0) + 1); // Kelime sayısını güncelle
    }

    // Kelime arama
    public boolean search(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node = node.children.get(c);
            if (node == null) {
                return false;
            }
        }
        return node.isEndOfWord;
    }

    // Belirli bir başlangıçla kelime arama
    public boolean startsWith(String prefix) {
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            node = node.children.get(c);
            if (node == null) {
                return false;
            }
        }
        return true;
    }

    // Dosyadan kelimeleri trie'ye ekleme
    public void insertFromFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] words = line.split("\\s+");
            for (String word : words) {
                insert(word.toLowerCase()); // Kelimeleri küçük harf yaparak ekleyelim
            }
        }
        reader.close();
    }

    // Aranan kelimenin trie'de olup olmadığını kontrol eden fonksiyon
    public boolean Search(String arg) {
        return search(arg.toLowerCase());
    }

    
    public void autoComplete(String prefix) {
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            node = node.children.get(c);
            if (node == null) {
                System.out.println("No words");
                return;
            }
        }
        List<String> results = new ArrayList<>();
        findAllWords(node, new StringBuilder(prefix), results);
        results.sort(String::compareTo); // Leksikografik olarak sırala
        for (int i = 0; i < results.size(); i++) {
            String word = results.get(i);
            if (word.indexOf(";") > 0) {
                word = word.substring(0, word.length() - 1);
            }
            
            System.out.print(word);
            
            // Son kelime değilse virgül ekle
            if (i < results.size() - 1) {
                System.out.print(", ");
            }
        }
    }

    // Yardımcı fonksiyon: Alt trie'deki tüm kelimeleri bulur
    private void findAllWords(TrieNode node, StringBuilder prefix, List<String> results) {
        if (node.isEndOfWord) {
            results.add(prefix.toString());
        }
        for (char c : node.children.keySet()) {
            prefix.append(c);
            findAllWords(node.children.get(c), prefix, results);
            prefix.deleteCharAt(prefix.length() - 1); // Son karakteri kaldır
        }
    }

    // Verilen suffix ile biten tüm kelimeleri leksikografik olarak yazdırır
    public void reverseAutoComplete(String suffix) {
        Trie reverseTrie = new Trie();
        List<String> allWords = new ArrayList<>();
        collectAllWords(root, new StringBuilder(), allWords);
        for (String word : allWords) {
            reverseTrie.insert(new StringBuilder(word).reverse().toString());
        }
        reverseTrie.autoComplete(new StringBuilder(suffix).reverse().toString());
    }

    // Yardımcı fonksiyon: Tüm kelimeleri toplar
    private void collectAllWords(TrieNode node, StringBuilder prefix, List<String> results) {
        if (node.isEndOfWord) {
            results.add(prefix.toString());
        }
        for (char c : node.children.keySet()) {
            prefix.append(c);
            collectAllWords(node.children.get(c), prefix, results);
            prefix.deleteCharAt(prefix.length() - 1); // Son karakteri kaldır
        }
    }

    // Verilen prefix ile başlayan ve suffix ile biten tüm kelimeleri yazdırır
    public void fullAutoComplete(String prefix, String suffix) {
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            node = node.children.get(c);
            if (node == null) {
                System.out.println("No words found with prefix: " + prefix);
                return;
            }
        }
        List<String> results = new ArrayList<>();
        findAllWords(node, new StringBuilder(prefix), results);
        results.sort(String::compareTo); // Leksikografik olarak sırala
        for (String word : results) {
            if (word.endsWith(suffix)) {
                System.out.println(word);
            }
        }
    }

    // En çok tekrar eden k kelimeyi yazdırır
    public void findTopK(int k) {
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(wordCount.entrySet());
        // Tekrar sayılarına göre sıralama, sonra leksikografik olarak sıralama
        entries.sort((e1, e2) -> {
            int countComparison = Integer.compare(e2.getValue(), e1.getValue());
            if (countComparison != 0) {
                return countComparison;
            }
            return e1.getKey().compareTo(e2.getKey());
        });
        for (int i = 0; i < k && i < entries.size(); i++) {
            System.out.println(entries.get(i).getKey() + ": " + entries.get(i).getValue());
        }
    }

    public static void main(String[] args) {
        Trie trie = new Trie();
        Scanner k = new Scanner(System.in);
        String file = k.nextLine();
        String filePath = file; // Dosya yolunu buraya ekleyin

       

        try {
            trie.insertFromFile(filePath);
            } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }


        String option = k.nextLine();
        
        String[] words = option.split(" ");


        if(words[0].equals("search")){
            System.out.println( trie.Search(words[1]));

        }else if (words[0].equals("autocomplete")){

            trie.autoComplete(words[1]);

        }else if (words[0].equals("reverse")){
            trie.reverseAutoComplete(words[1]);

        }else if(words[0].equals("full")){
            trie.fullAutoComplete(words[1], words[2]);
        }else if(words[0].equals("topk")){
            trie.findTopK(Integer.parseInt(words[1]));
        }

        
        
    }


    class TrieNode {
        public Map<Character, TrieNode> children;
        public boolean isEndOfWord;
    
        public TrieNode() {
            children = new HashMap<>();
            isEndOfWord = false;
        }
    }


}
