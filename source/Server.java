import java.util.Scanner;

class Server{
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("id: ");
        TOTP token = new TOTP(scanner.next());
        scanner.reset(); // flush buffer
        
        // token.setInterval(30); 
        token.setDigits(8); 
        
        System.out.print("Token: ");
        
        while(true){
            String userToken = scanner.next();
            
            if ( 
                    userToken.equals(token.getToken(false).toString()) || // com timestamp atual
                    userToken.equals(token.getToken(true).toString())     // adiciona uma tolerância de INTERVAL * 2 (e.g. 8 segundos = 16)
            ){

                System.out.print("Token válido");
                break;
                
            }else{
                System.out.print("Token inválido. Digite Novamente: ");
            }
        }
    }
}
