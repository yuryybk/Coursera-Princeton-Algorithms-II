public class Outcast {

   private final WordNet wordNet;

   // constructor takes a WordNet object
   public Outcast(WordNet wordnet) {
      this.wordNet = wordnet;
   }

   // given an array of WordNet nouns, return an outcast
   public String outcast(String[] nouns) {
      int[] dist = new int[nouns.length];
      for (int v = 0; v < nouns.length; v++) {
         for (int w = 0; w < nouns.length; w++) {
            if (v != w) {
               dist[v] += wordNet.distance(nouns[v], nouns[w]);
            }
         }
      }

      int index = -1;
      int max = 0;
      for (int i = 0; i < dist.length; i++) {
         if (dist[i] > max) {
            max = dist[i];
            index = i;
         }
      }
      return nouns[index];
   }

   public static void main(String[] args) {

   }
}