/**
 * Author: Van Pham
 * Version: 1
 * Description: A simulation of the game Passover Coin Shift. It will determine
 * if the first player or the second player will win the game. If the outcome class
 * of the game is PREV, then the second player wins. Otherwise, first player wins.
 */

import java.util.*;

public class Game {
    // The positions of the coins in the game
    List<Integer> list;
    // The list of all the P-positions
    private static Set<String> pList = new HashSet<>();
    // The list of all calculated positions, to speed up calculation
    private static Map<String, Outcome> cache = new HashMap<>();

    enum Outcome{
        NEXT,
        PREV,
    }
    // The outcome of this game
    private Outcome outcome = null;

    /**
     * A constructor of the game
     * @param list the list of the positions of each coin
     */
    public Game(List<Integer> list){
        this.list = list;
        rearrange();
        if(cache.containsKey(this.toString())){
            outcome = cache.get(this.toString());
        } else {
            calcOutcome();
        }
    }

    /**
     * Get the P-positions of this game
     * @return a set of P-positions
     */
    public Set getPpositions(){
        return pList;
    }

    /**
     * Get the outcome of this game
     * @return the outcome of the game
     */
    public Outcome getOutcome(){
        return outcome;
    }

    /**
     * Calculate the outcome of the game by finding its subgames then if 1 of
     * its subgames is a P-position, then this game is a N-position. Otherwise,
     * this game is a P-position.
     *
     * Post-condition:
     *      if this game is a P-position, it will be added to the P-position set.
     */
    private void calcOutcome(){
        if (outcome == null) {
            List<Outcome> subGames = getSubGames();
            Outcome temp = Outcome.PREV;
            for (Outcome o : subGames) {
                if (o == Outcome.PREV) {
                    temp = Outcome.NEXT;
                    break;
                }
            }
            outcome = temp;
        }
        if (outcome == Outcome.PREV){
            pList.add(this.toString());
        }
    }

    /**
     * Do a recursive loop to find whether each subgame is a P-position or a N-position
     * @return a list of the outcomes of all the subgames.
     * Post-condition:
     *      While doing the loop, this function will remember the result of previously
     *      calculated games. So it does not do the calculation twice.
     */
    private List<Outcome> getSubGames(){
        List<Outcome> subGames = new ArrayList<>();
        this.rearrange();
        for(int a = 0; a < list.size(); a++) {
            int coin = list.get(a); // Get the coin position
            for (int i = coin - 1; i > 0; i--) {
                // i is the new position of the coin
                // Get the sub positions of this game by moving this coin leftward (or downward)
                if (!list.contains(i)) {
                    List<Integer> subList = this.getSubList(a, i);
                    Game sub = new Game(subList);
                    Outcome o = sub.getOutcome();
                    subGames.add(o);
                    if (!cache.containsKey(sub.toString())) {
                        cache.put(sub.toString(), o);
                    }
                }
            }
        }
        return subGames;
    }

    /**
     * This create a sub position of the original position of the coins. The coin at
     * the specified index will be moved to the specified position
     *
     * @param index the index of the coin
     * @param position  new position of the coin
     * @return a list containing the positions of the coins after the move
     *
     */
    private List<Integer> getSubList(int index, int position){
        List<Integer> subList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++){
            if(index == i){
                subList.add(position);
            } else{
                subList.add(list.get(i));
            }
        }
        return subList;
    }

    /**
     * Sort the list so that the coins in order
     */
    private void rearrange(){
        Collections.sort(list);
    }

    /**
     * The game will be formated as (a, b, c, d, ...)
     * where a is the position of the leftmost coin and so on
     * @return the string format above
     */
    @Override
    public String toString(){
        if (list.isEmpty()){
            return "()";
        }
        String result = "(";
        for (int i = 0; i < list.size()-1; i++){
            result = result.concat(list.get(i).toString());
            result = result.concat(", ");
        }
        result = result.concat(list.get(list.size()-1).toString());
        result = result.concat(")");
        return result;
    }

    /**
     * Get a list of coin positions from the user
     * @return a list of positions
     */
    public static List<Integer> getListFromUser(){
        Scanner sc = new Scanner(System.in);
        List<Integer> list = new ArrayList<>();
        System.out.print("The number of coins in the game (larger than 0): ");
        try{
            int c = sc.nextInt();
            if (c <= 0){
                throw new Exception();
            }
            for(int i = 1; i <=c ; i++){
                System.out.print("Coin "+i+ ": ");
                list.add(sc.nextInt());
            }
        } catch (Exception e){
            System.err.println("You did not enter an integer or a valid one");
            System.exit(1);
        }
        sc.close();
        return list;
    }

    public static void main(String[] args){
        List<Integer> list = getListFromUser();
        System.out.println("Processing...");
        Game g = new Game(list);
        System.out.println("The outcome class of this game: " + g.getOutcome());
        System.out.println("The P-positions are: " + g.getPpositions());
    }
}
