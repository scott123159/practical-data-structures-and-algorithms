class RPG {
    int[] attack;
    int[] defence;
    public RPG(int[] defence, int[] attack){
        this.attack = new int[attack.length];
        this.defence = new int[defence.length];
        for (int i = 0; i < defence.length; i++) {
            this.attack[i] = attack[i];
            this.defence[i] = defence[i];
        }
    }
    public int maxDamage(int rounds) {
        int[] dpa = new int[rounds];
        int[] dpb = new int[rounds];
        dpa[0] = attack[0] - defence[0];
        dpb[0] = 0;
        for (int i = 1; i < rounds; i++) {
            dpa[i] = Math.max(dpb[i - 1] + 2 * attack[i] - defence[i], dpa[i - 1] + attack[i] - defence[i]);
            dpb[i] = Math.max(dpa[i - 1], dpb[i - 1]);
        }
        return Math.max(dpa[rounds - 1], dpb[rounds - 1]);
    }

}

class HW2 {
    public static void main(String[] args) {
        RPG sol = new RPG(new int []{100,20},new int []{235,234});
        System.out.println(sol.maxDamage(1));
    }
}