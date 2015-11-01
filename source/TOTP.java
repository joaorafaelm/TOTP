import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Formatter;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;

/**
 * TOTP (Time-based one-time password algorithm) [RFC 6238 - 2011]
 * HOTP (HMAC one-time password algorithm) [RFC 4226 - 2005]
 * HMAC (Hash-key massage authentication code) [RFC 2104 - 1997]
 */
public class TOTP {
	private final String ALGORITHM = "HmacSHA256";
    private String SECRET;
	private int INTERVAL = 8;
	private int DIGITS = 8;
	
	public TOTP(String secret){
		this.SECRET = secret;
	}
	
	/**
	* Converte array de bytes para hexadecimal
	* @param  bytes - conjunto de bytes 	
	* @return         retorna em hexadecimal
	*/
	private String toHexString(byte[] bytes) {
		Formatter formatter = new Formatter();
		for (byte b : bytes) {
			formatter.format("%02x", b);
		}
		return formatter.toString();
	}
    
	/**
	* Intervalo de tempo que o token será válido
	* @param interval Número em segundos.
	*/
	public void setInterval(int interval){
		this.INTERVAL = interval;
	}
	
	/**
	* @param digits Número de dígitos que terá o token
	*/
	public void setDigits(int digits){
		this.DIGITS = digits;
	}
	
    /**
    * HMAC
    * @param String interval - intervalo em segundos extraído do timestamp
    * @return string hexadecimal de 160bits 
    */
	public String HMAC(String interval)
		throws SignatureException, NoSuchAlgorithmException, InvalidKeyException
	{
		SecretKeySpec signingKey = new SecretKeySpec(
			this.SECRET.getBytes(), ALGORITHM
		);
		
		Mac mac = Mac.getInstance(ALGORITHM);
		mac.init(signingKey);
		
		return toHexString(
			mac.doFinal( interval.getBytes() )
		);
	}
	
	/**
	* Retorna timestamp com um intervalo de x segundos, sendo x = step;
	* @return tempo atual / intervalo
	*/
	private String unixTimestampInterval(){
        return Long.toString(
			System.currentTimeMillis() / 1000L / this.INTERVAL
		); 	
	}

	/**
	* Retorna uma string de 32 bits derivada do hash;
	* O offset é definido pelo valor dos últimos 4 bits;
	*
	* @param   hash String de 160 bits
	* @return       Número hexadecimal de 32-bit
	*/
	private String truncateHash(String hash){
		
		// Valor decimal do último byte do HMAC
        int offset = Character.getNumericValue(
            hash.charAt(
                hash.length() - 1
            )
        );
        
        // hex string truncated from hash
        return hash.substring(
            offset, offset + 8
        );
	}
	
	/**
	* Retorna o token com o número de digitos pre especificado (padrão = 8)
	* @param boolean delay - Se delay for igual a 1 - então aceita o token por mais 8 segundos
	* @return String 
	*/
	public BigInteger getToken(boolean delay) throws Exception {
		
		// intervalo em segundos
        String interval = unixTimestampInterval();
		
		/*
		* 
		* Decrementa o valor do timestamp, 
		* considera o token antigo por mais 8 segundos
		* e converte novamente para String.
		*
		*/
		if(delay){
			int interval_integer = Integer.parseInt(interval) - 1;
			interval = interval_integer + "";
		}

        // 160 bit string
		String hmac = HMAC(interval);

		// Dynamic truncate de 160bits pra 32bit
		String truncated_hmac = this.truncateHash(hmac);
        
        // Converte 0x32-bit para decimal
        BigInteger dynamic_binary = new BigInteger(
			truncated_hmac, 
			16
		);
        
		
        BigInteger modulo = new BigInteger("10"); // exponent
        modulo = modulo.pow(this.DIGITS); // ^ n of digits
		
		// token mod 10 ^ n (onde n é o número de digitos do token)
        BigInteger token = dynamic_binary.mod( modulo );
        
        // System.out.println("Timestamp \t" + interval);
        // System.out.println("HMAC-SHA1 \t" + hmac);
        // System.out.println("Truncated \t" + truncated_hmac);
        // System.out.println("DB        \t" + dynamic_binary);
        // System.out.println("Token     \t" + token );
        
        return token;
        
	}

}
