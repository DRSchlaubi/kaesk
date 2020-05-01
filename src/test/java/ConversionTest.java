import java.util.Arrays;
import java.util.stream.Collectors;
import me.schlaubi.kaesk.api.ArgumentDeserializer;
import me.schlaubi.kaesk.api.converters.Converters;
import org.bukkit.OfflinePlayer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConversionTest {

  @Test
  public void parseNormalInt() {
    testPass(1, Converters.INTEGER, Integer.class);
    testPass(10, Converters.INTEGER, Integer.class);
  }

  @Test
  public void parseInvalidInt() {
    testFail("Not A number", Converters.INTEGER, Integer.class);
    testFail("10.0", Converters.INTEGER, Integer.class);
  }

  @Test
  public void parseNormalInts() {
    testVarargPass(new Integer[]{1, 2, 3, 4, 5, 79}, Converters.INTEGER, Integer.class);
  }

  @Test
  public void parseInvalidInts() {
    testVarArgFail("1.0 1 9", Converters.INTEGER, Integer.class);
    testVarArgFail("not a number", Converters.INTEGER, Integer.class);
  }

  @Test
  public void parseNormalFloat() {
    testPass(1F, Converters.FLOAT, Float.class);
    testPass(10F, Converters.FLOAT, Float.class);
  }

  @Test
  public void parseInvalidFloat() {
    testFail("Not A number", Converters.FLOAT, Float.class);
  }

  @Test
  public void parseNormalFloats() {
    testVarargPass(new Float[]{1F, 2F, 3F, 4F, 5F, 79F}, Converters.FLOAT, Float.class);
  }

  @Test
  public void parseInvalidFloats() {
    testVarArgFail("not a number", Converters.FLOAT, Float.class);
  }

  @Test
  public void parseNormalDouble() {
    testPass(1D, Converters.DOUBLE, Double.class);
    testPass(10D, Converters.DOUBLE, Double.class);
  }

  @Test
  public void parseInvalidDouble() {
    testFail("Not A number", Converters.DOUBLE, Double.class);
  }

  @Test
  public void parseNormalDoubles() {
    testVarargPass(new Double[]{1D, 2D, 3D, 4D, 5D, 79D}, Converters.DOUBLE, Double.class);
  }

  @Test
  public void parseInvalidDobules() {
    testVarArgFail("not a number", Converters.FLOAT, Float.class);
  }

  @Test
  public void parseNormalLong() {
    testPass(1L, Converters.LONG, Long.class);
    testPass(10L, Converters.LONG, Long.class);
  }

  @Test
  public void parseInvalidLong() {
    testFail("Not A number", Converters.LONG, Long.class);
  }

  @Test
  public void parseNormalLongs() {
    testVarargPass(new Long[]{1L, 2L, 3L, 4L, 5L, 79L}, Converters.LONG, Long.class);
  }

  @Test
  public void parseInvalidLongs() {
    testVarArgFail("not a number", Converters.LONG, Long.class);
  }

  @Test
  public void testIllegalPlayerNames() {
    testFail("Inval$id", Converters.OFFLINE_PLAYER, OfflinePlayer.class);
    testFail("to", Converters.OFFLINE_PLAYER, OfflinePlayer.class);
    testFail("sh", Converters.OFFLINE_PLAYER, OfflinePlayer.class);
    testFail("or", Converters.OFFLINE_PLAYER, OfflinePlayer.class);
    testFail("t", Converters.OFFLINE_PLAYER, OfflinePlayer.class);
    testVarArgFail("Inval$id id did __ ,,", Converters.OFFLINE_PLAYER, OfflinePlayer.class);
    testVarArgFail("to sh o rt", Converters.OFFLINE_PLAYER, OfflinePlayer.class);
  }

  private <T> void testVarargPass(T[] input, ArgumentDeserializer<T> deserializer, Class<?> clazz) {
    var args = Arrays.stream(input).map(Object::toString).collect(Collectors.joining(" "));
    var parsed = testVararg(args, deserializer, true, clazz);
    Assertions.assertArrayEquals(input, parsed, "Parsed value should be the same");
  }

  private <T> void testVarArgFail(String input, ArgumentDeserializer<T> deserializer, Class<?> clazz) {
    testVararg(input, deserializer, false, clazz);
  }

  private <T> T[] testVararg(String input, ArgumentDeserializer<T> deserializer, boolean isValid, Class<?> clazz) {
    var args = input.split("\\s+");
    Assertions.assertEquals(isValid, deserializer.varargIsValid(args, clazz),
        "Is valid is expected to be %s".formatted(isValid));
    if (!isValid) {
      return null;
    }
    return deserializer.deserializeVararg(args, clazz);
  }

  private <T> void testPass(T input, ArgumentDeserializer<T> deserializer, Class<?> clazz) {
    T parsed = test(input.toString(), deserializer, true, clazz);
    Assertions.assertEquals(input, parsed, "Parsed value should be the same");
  }

  private <T> void testFail(String input, ArgumentDeserializer<T> deserializer, Class<?> clazz) {
    test(input, deserializer, false, clazz);
  }

  private <T> T test(String input, ArgumentDeserializer<T> deserializer, boolean isValid, Class<?> clazz) {
    Assertions.assertEquals(isValid, deserializer.isValid(input, clazz),
        "Is valid is expected to be %s".formatted(isValid));
    if (!isValid) {
      return null;
    }
    return deserializer.deserialize(input, clazz);
  }

}
