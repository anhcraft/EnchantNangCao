package org.anhcraft.enchantnangcao.manager;

public enum EnchantType {
    TRAP("Bẫy"),
    SKILL("Kỹ năng"),
    DEFAULT("Mặc định"),
    AUTO_ACTIVATE("Tự động kích hoạt");

    private String s;

    EnchantType(String s){
        this.s = s;
    }

    public String str() {
        return this.s;
    }
}
