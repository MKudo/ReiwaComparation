package io.github.mkudo.compare;

import java.lang.Character.UnicodeBlock;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class Reiwa {
	public static final EraName REIWA_NORMAL = new EraName("令和", "統合漢字");
	public static final EraName REIWA_KS_X_1001 = new EraName("令和", "KS_X_1001");
	public static final EraName REIWA_VS_FE00 = new EraName("令︀和", "VS_FE00");
	public static final EraName REIWA_VS_E0100 = new EraName("令󠄀和", "VS_E0100");
	public static final EraName REIWA_VS_E0101 = new EraName("令󠄁和", "VS_E0101");
	public static final EraName REIWA_VS_E0102 = new EraName("令󠄂和", "VS_E0102");
	// セクション的には Enclosed CJK Letters and Months じゃないか？
	public static final EraName REIWA_CJK_COMPATIBILITY = new EraName(Character.toString(0x32ff), "合字");

	private static final List<EraName> REIWA_VALIANTS = Collections.unmodifiableList(Arrays.asList(REIWA_NORMAL,
			REIWA_KS_X_1001, REIWA_VS_FE00, REIWA_VS_E0101, REIWA_VS_E0102, REIWA_CJK_COMPATIBILITY));

	public static void main(String args[]) {
		testAll("do nothing", (new DoNothing()));
		testAll("NFKC normalize", (new NFKCNormalize()));
		testAll("NFKC normalize + Remove VS", (new NFKCNormalize()).andThen(new RemoveVS()));

		// ㍻ も normalize だけで行けるので、将来的には REIWA_CJK_COMPATIBILITY も normalize だけで通るはず
		System.out.printf("㍻ is 平成 ? : %b", Normalizer.normalize("㍻", Normalizer.Form.NFKC));
	}

	private static final class EraName {
		final String name;
		final String type;

		EraName(final String name, final String type) {
			this.name = name;
			this.type = type;
		}

		@Override
		public String toString() {
			return "EraName [name=" + name + ", type=" + type + "]";
		}
	}

	private static final class DoNothing implements Function<EraName, EraName> {
		@Override
		public EraName apply(final EraName in) {
			return in;
		}
	}

	private static final class NFKCNormalize implements Function<EraName, EraName> {
		@Override
		public EraName apply(final EraName in) {
			return new EraName(Normalizer.normalize(in.name, Normalizer.Form.NFKC), in.type);
		}
	}

	private static class RemoveVS implements Function<EraName, EraName> {
		@Override
		public EraName apply(final EraName in) {
			StringBuilder sb = new StringBuilder();

			in.name.codePoints().filter(point -> {
				UnicodeBlock block = UnicodeBlock.of(point);
				return !((block == UnicodeBlock.VARIATION_SELECTORS)
						|| (block == UnicodeBlock.VARIATION_SELECTORS_SUPPLEMENT));
			}).forEach(point -> sb.append(Character.toChars(point)));

			return new EraName(sb.toString(), in.type);
		}
	}

	private static void testAll(String testType, Function<EraName, EraName> f) {
		System.out.printf("=== Test [%s] pattern passed list%n", testType);
		REIWA_VALIANTS.stream().map(f)
				.filter(s -> s.name.equals(REIWA_NORMAL.name) || s.name.equals(Character.toString(0x32ff)))
				.forEach(System.out::println);
		System.out.printf("=== Test [%s] pattern list end%n%n", testType);
	}
}
