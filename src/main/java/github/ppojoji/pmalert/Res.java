package github.ppojoji.pmalert;

/**
 * TODO Util.map(..), Util.success(..) 종류의 메서드 호출을 Res 클래스로 빼낼 예정
 */
public class Res {

	public static TypeMap fail() {
		TypeMap m = new TypeMap(1);
		m.put("success", false);
		return m;
	}
    public static TypeMap success(boolean success) {
        TypeMap m = new TypeMap(1);
        m.put("success", success);
        return m;
    }

    public static TypeMap success(Object ... args) {
        TypeMap m = new TypeMap(1 + args.length/2);
        m.put("success", true);
        return build(m, args);
    }
    public static TypeMap fail(Object ... args) {
        TypeMap m = new TypeMap(1 + args.length/2);
        m.put("success", false);
        return build(m, args);
    }
    
    private static TypeMap build(TypeMap m, Object ... args) {
        if(args.length % 2 == 1) {
            throw new RuntimeException("(key, value) mismatch: ");
        }
        for (int i = 0; i < args.length; i+=2) {
            Object k = args[i];
            Object v = args[i+1];
            if(k.getClass() != String.class) {
            	// FIXME PmException 클래스로 대체해야함
            	throw new RuntimeException("500: SERVER_ERROR");
            	/*
                throw new AacException(
                        500, 
                        "SERVER_ERROR"
                        );
                 */
            }
            m.put((String)k, v);
        }
        return m;
    }

}
