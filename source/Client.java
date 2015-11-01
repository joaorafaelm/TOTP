class Client{
    public static void main(String[] args) throws Exception {
        TOTP token = new TOTP("1228993198");
        token.setInterval(8); // default value for interval 
        token.setDigits(8); 
        while(true){
            System.out.print(
                " Token     \t" + token.getToken(false) + "\r" 
            );
            Thread.sleep(1000);
        }
    }
}
