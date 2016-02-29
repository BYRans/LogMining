package AprioriEnd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ApriorilesEnd {

	public static SimpleDateFormat DATE_TEMPLATE = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	public static List<List<String>> TIME_LABEL_LIST = new ArrayList<List<String>>();// 闁汇垻鍠愰崹姘鐎ｎ亜顫ら柣銊ュ濡晞绠涘鍓х殤濞寸媴鎷�
	public static List<List<String>> transactions = new ArrayList<List<String>>(); // 濞存粌顑呮慨鐔告償韫囨挸鐏�
	public static int windowSize = 120; // 缂佹劖顨呰ぐ娑欏緞瑜嶉惃锟�
	public static int stepSize = 30; // 缂佸顕ф慨鈺侇潰閵夆晜姣�
	public static long getKItemTime; // 閻庢稒蓱閺備線骞嶉敓鑺ョ畳闁稿﹥鐟╅敓鑺ャ亜瑜版帗鑲犻柣銊ュ缁ㄣ劍绂掗懜鍨
	public static List<List<Integer>> supporCount = new ArrayList<List<Integer>>(); // 閻庢稒蓱閺備礁袙韫囧酣鍤嬪Λ鐗堝灩缁犳帗銇勮ぐ鎺撹偁闁汇劌瀚花銊︾閼稿灚娈�
	public static List<List<List<String>>> eventRules = new ArrayList<List<List<String>>>(); // 閻庢稒蓱閺備線骞嶉敓鑺ョ畳闁汇劌瀚花銊︾閹偊娼愰柛鎺炴嫹
	static boolean endTag = false; // 鐎甸偊浜為獮鍡涘及椤栨碍鍎婇弶鈺傜椤㈡垿鎯冮崟顒傚灱闊浄鎷�
	final static double MIN_SUPPORT = 2; // 闁猴拷鍨辩�鏃�償閿曞倹顫岄柛濠忔嫹濞存粌顑勫▎銏ゅ极閿燂拷
	final static double MIN_CONF = 0.5; // 缂傚喚鍠曟穱濠冩償閿曞倹顫岄柛濠忔嫹
	public static List<List<String>> satisfiedConfidence = new ArrayList<List<String>>(); // 閻庢稒蓱閺備胶鎮伴妸褋浠涙繝濞愩倕鍠曠紓鍐惧枙娣囧﹥鎯旈敃鍌涱潓闁稿﹨鍋愬▓锟藉ù婊冾儎濞嗐垻鎲撮崟顐㈢仧)濡増鍨圭粻鎺撱亜閸︻厽鐣辩紓鍐惧枙娣囧﹥鎯旈敓锟�
	public static String exportConfidence = new String();
	public static int eventCount = 0;

	/**
	 * @param args
	 * @throws ParseException
	 */
	public static void main(String[] args) throws ParseException {
		String mergeLog = "C:/Users/zys/Desktop/20150501DF_Bsort.txt";
		String result = "C:/Users/zys/Desktop/4ItemsW120ST30SU2C0.5-Ip20150501DF_BResult.txt";

		getRecord(mergeLog);
		long startTime = System.currentTimeMillis();

		getTransactions(windowSize, stepSize);
		System.out.println("Get Transactions Time:"
				+ (System.currentTimeMillis() - startTime) + "ms");

		List<String> oneFItemset = findFirstFrequenItemset();
		System.out.println("Genetate 1 Item Time:"
				+ (System.currentTimeMillis() - startTime) + "ms");

		long generate1ItemTime = System.currentTimeMillis();
		List<List<String>> cItemset = findSencondCandidate(oneFItemset);
		System.out.println("================");

		List<List<String>> fItemset = getSupportedItemset(cItemset);
		System.out.println("++++++++++++++++");
		getSecondConfidencedItemset(fItemset, oneFItemset); //
		long generate2EventTime = System.currentTimeMillis();
		System.out.println("Genetate 2 Event Time:"
				+ (generate2EventTime - generate1ItemTime) + "ms");

		long generateKItemTime = System.currentTimeMillis();
		int ItemCount = 2;
		while (endTag != true) {
			ItemCount++;
			System.out.println(ItemCount);
			List<List<String>> ckItemset = getNextCandidate(fItemset);
			List<List<String>> fkItemset = getSupportedItemset(ckItemset);
			getConfidencedItemset(fkItemset, fItemset);
			fItemset = fkItemset;
			if (fItemset.get(0).size() >= 3)
				break;
		}
		System.out.println("Genetate K Item Time:"
				+ (System.currentTimeMillis() - generateKItemTime) + "ms");

		long generatePrintTime = System.currentTimeMillis();
		print(result);
		System.out.println("Genetate print Item Time:"
				+ (System.currentTimeMillis() - generatePrintTime) + "ms");
		System.out.println("Total Time:"
				+ (System.currentTimeMillis() - startTime) + "ms");
		System.out.print("finished!");

	}

	/**
	 * 打印满足置信度的事件规则
	 * 
	 * @param result
	 */
	public static void print(String result) {
		String eventRulesAndconfidenceFile = result;

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					eventRulesAndconfidenceFile), false));

			for (int i = 0; i < eventRules.size(); i++) {// K
				for (int j = 0; j < eventRules.get(i).size(); j++) { // Kxiagnjigeshu1
					for (int k = 0; k < eventRules.get(i).get(j).size(); k++) {
						writer.write(eventRules.get(i).get(j).get(k) + "\t");
					}
					writer.write(satisfiedConfidence.get(i).get(j) + "\r\n");
				}

			}

			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 读取合并好的文件
	 * 
	 * @param mergeLog
	 */
	private static void getRecord(String mergeLog) {
		// int count = 0;
		String TimeSortedLogFile = mergeLog;
		try {
			// File File = new File(TimeSortedLogFile);
			BufferedReader wReader = new BufferedReader(new InputStreamReader(
					new FileInputStream(TimeSortedLogFile), "UTF-8"));
			String line = null;
			while ((line = wReader.readLine()) != null) {
				if ("".equals(line.trim())) { // 闂侇剙娲ㄩ埞鏍偘瀹�绀夐柣锝冨劥缁诲啰绱掕閻㈠骞嶈椤拷
					continue;
				}
				eventCount++;
				List<String> tempList = new ArrayList<String>();
				String[] logArr = line.split("\t");
				tempList.add(logArr[0]);
				tempList.add(logArr[1]);
				TIME_LABEL_LIST.add(tempList);
			}
			wReader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	/**
	 * 将事件规则转换为事务
	 * 
	 * @param windowSize
	 * @param stepSize
	 * @throws ParseException
	 */
	public static void getTransactions(int windowSize, int stepSize)
			throws ParseException {
		List<List<String>> tempTransactions = new ArrayList<List<String>>();
		int logMergeListLength = TIME_LABEL_LIST.size(); // 闁轰胶澧楀畵渚�⒖閸℃ぜ浜ｉ悘蹇ユ嫹
		Date minDate = DATE_TEMPLATE.parse(TIME_LABEL_LIST.get(0).get(0)); // 0閻庢稒蓱閺備線鎯冮崟顒佇﹂柡鍐ㄧ埣濡拷
		Date endDate = DATE_TEMPLATE.parse(TIME_LABEL_LIST.get(
				logMergeListLength - 1).get(0));
		Calendar cal = Calendar.getInstance();
		cal.setTime(minDate);
		cal.add(Calendar.MINUTE, windowSize);
		Date maxDate = cal.getTime();
		Date stepDate = cal.getTime();
		int curIndex = 0;// 鐟滅増鎸告晶鐘诲籍閸洘锛�
		int stepIndex = 0;// 缂佸顕ф慨鈺傜▔閿熶粙鍤嬫慨婵勫劦閺嗛亶宕ユ惔銏狀暡闁革负鍔庡▓鎴﹀籍閸洘锛�
		while (minDate.getTime() < endDate.getTime()) {
			// 濞ｅ洦绻傞悺銊ㄣ亹閹惧啿顤呴柡鍐ㄧ埣濡法绮ｅΔ锟界稉闁告劕鎳愬▓鎴犵尵鐠囨彃鐒奸柡浣哄瀹撲線宕氶惃濂簍闂傚棗妫楅幃搴㈢▔閿燂拷
			List<String> curLogSet = new ArrayList<String>(); // 闁猴拷顫夐崹姘舵偨閳獙st閻庢稒锚閸嬪秹鏁嶇仦鐣屾憼闁稿被鍔戦崳鍛婂緞瀹ュ懎甯楃紒鎲嬫嫹
			curIndex = stepIndex; // 闁哄啨鍎辩换鏃傛閵忕姷绌块柛娆戞焿缁佹挳宕愰梻纾嬬濞戞挸锕ｇ粩瀛樼▔椤忓拋鍔勯梻锟界仢閸ゎ參鎯冮崟顓炲亶鐎殿喗娲栬ぐ鍧楁晬鐏炲墽妲ㄦ繛鍡忥拷椤ゅ啴宕濋悩杈╊伇濞戞搩浜濋鐐烘⒐閿燂拷
			cal.setTime(minDate);
			cal.add(Calendar.MINUTE, stepSize);
			stepDate = cal.getTime();
			for (int i = curIndex; i < TIME_LABEL_LIST.size(); i++) {
				Date timeStamp = DATE_TEMPLATE.parse(TIME_LABEL_LIST.get(i)
						.get(0));
				if (timeStamp.getTime() >= minDate.getTime()
						&& timeStamp.getTime() < maxDate.getTime()) {
					curLogSet.add(TIME_LABEL_LIST.get(i).get(1));
				} else if (timeStamp.getTime() >= maxDate.getTime()) {
					break;
				}
				if (timeStamp.getTime() <= stepDate.getTime()) {
					stepIndex++; // 濠碘�鍊归悘澶愬籍閸洘锛熼柛锔哄妼缂嶅宕滃鍡╁妱闂傦拷鐏濋崬鎾晬鐏炵偓锛夐煫鍥殘閸屻劌顕ｉ弴鐐插▏闁告梻濮崇粩锟�
				}
			}
			// 缂佹劖顨呰ぐ娑㈠嫉閿熷浜ｉ柡鍫嫹閻剟寮崼鏇燂紵濞戞挸顑嗙划锔界▔閿熶粙鍤嬫慨婵勫劦閺嗭拷
			cal.setTime(minDate);
			cal.add(Calendar.MINUTE, stepSize);
			minDate = cal.getTime();
			cal.setTime(maxDate);
			cal.add(Calendar.MINUTE, stepSize);
			maxDate = cal.getTime();
			tempTransactions.add(curLogSet);
		}
		for (int i = 0; i < tempTransactions.size(); i++) { // 闁告ê顭峰▍搴㈢鐎ｎ亜顫ゅ☉鎿冨幒鐠愮喓绮氶搹瑙勭暠閻庢稒鍔曠花顓㈠礆閿燂拷
			// System.out.print("閺夆晜绋戦崣鍞僥tTransaction闁告垼濮ら弳锟�;
			if (tempTransactions.get(i).size() != 0) {
				transactions.add(tempTransactions.get(i));
			}
		}
	}

	/**
	 * 生成频繁一项集
	 * 
	 * @return oneFItemset
	 */
	public static List<String> findFirstFrequenItemset() {
		int count = 1;
		Map<String, Integer> oneCandidateItemSetAndCount = new HashMap<String, Integer>();
		Map<String, Integer> oneFrequentItemSetAndCount = new HashMap<String, Integer>();
		for (List<String> transactionList : transactions) { // 濞寸姴楠搁幃鍥ㄧ▔椤忓啰鐨戦柛鏂猴拷閼垫垵顕ュΔ锟界厒濡増鍨圭粻鎺撶▔閿熷锟介梻鍡嫹
			for (String tempStr : transactionList) {
				count = 1;
				if (oneCandidateItemSetAndCount.containsKey(tempStr)) {
					count = (int) oneCandidateItemSetAndCount.get(tempStr) + 1;
				}
				oneCandidateItemSetAndCount.put(tempStr, count);
			}
		}

		List<Integer> anotherTempList = new ArrayList<Integer>();

		for (Entry<String, Integer> entry : oneCandidateItemSetAndCount // 婵懓鍊块。鍓佹崲娴ｉ顏卞銈呯秺濞夛拷
				.entrySet()) {
			anotherTempList.add(entry.getValue());
			if ((int) entry.getValue() > MIN_SUPPORT) {
				oneFrequentItemSetAndCount
						.put(entry.getKey(), entry.getValue());
			}
		}
		// allCandidateSupporCount.add(anotherTempList); //
		// 閻庢稒锚閸嬪秹骞嶉敓鑺ョ畳闁稿﹥鐟╅敓鑺ャ亜瑜版帗鑲犻柣銊ュ缁ㄣ劍绂掗懜鍨
		List<Integer> tempList = new ArrayList<Integer>();
		for (Entry<String, Integer> entry : oneFrequentItemSetAndCount
				.entrySet()) {
			tempList.add(entry.getValue());
		}
		supporCount.add(tempList); // 閻忓繐妫濋。鍓佹崲娴ｉ顏卞銈呯秺濞夛箓鎯冮崟顏嗙殤濞寸姾鍩栭弳鐔猴拷濡搫浜堕柛鎺旀疁upporCount濞戞搫鎷�
		List<String> oneFItemset = new ArrayList<String>();

		for (Entry<String, Integer> entry : oneFrequentItemSetAndCount
				.entrySet()) {
			oneFItemset.add(entry.getKey());// 閻忓繐妫濋。鍓佹崲娴ｉ顏卞銈呯秺濞夛箓鎯冮崟顐ｅ�濡炪倝锟界花銊︾鐠鸿櫣鎽犻柛灞诲妼閸╁neFItemset濞戞搫鎷�
		}
		for (Entry<String, Integer> entry : oneFrequentItemSetAndCount
				.entrySet()) {
			System.out.print(entry.getKey() + " ");
			System.out.print(entry.getValue() + "\r\n ");// 閻忓繐妫濋。鍓佹崲娴ｉ顏卞銈呯秺濞夛箓鎯冮崟顐ｅ�濡炪倝锟界花銊︾鐠鸿櫣鎽犻柛灞诲妼閸╁neFItemset濞戞搫鎷�
		}
		return oneFItemset;

	}

	/**
	 * 生成二项后选项集
	 * 
	 * @param oneFItemSet
	 * @return
	 */
	public static List<List<String>> findSencondCandidate(
			List<String> oneFItemSet) { // 闁汇垻鍠愰崹姘瀹�嫨锟介柛濠冪懇閿熻姤銇勮ぐ鎺撹偁
		List<List<String>> twoCandidateItemSet = new ArrayList<List<String>>();
		for (int i = 0; i < oneFItemSet.size(); i++) { // 闁汇垻鍠愰崹姘▔閿熻棄纾瑰ù婊冪焸閵嗗秹宕愬▎鎿勬嫹濡炪倕缍婂▔锟�
			for (int j = i + 1; j < oneFItemSet.size(); j++) {
				List<String> tempList = new ArrayList<String>();
				tempList.add(oneFItemSet.get(i));
				tempList.add(oneFItemSet.get(j));
				twoCandidateItemSet.add(tempList);
			}
		}

		int halfTwoCandidateItemSize = twoCandidateItemSet.size(); // 濞存粌鐭傞妴宥夊磹濞嗘搫鎷峰銈囨嚀閸樻挾妲愰悩灞備海閻忓繐绻掑▓鎴炵▔閿熻棄纾�
		for (int i = 0; i < halfTwoCandidateItemSize; i++) { // 闁兼儳鍢茶ぐ鍥ㄧ瀹�嫨锟介柛濠冪懇閿熻姤銇勮ぐ鎺撹偁闁汇劌瀚ぐ鐔哥▔閿熻棄纾�
			List<String> tempList = new ArrayList<String>();
			tempList.add(twoCandidateItemSet.get(i).get(1));
			tempList.add(twoCandidateItemSet.get(i).get(0));
			twoCandidateItemSet.add(tempList);
		}
		// allITtemSets.add(twoCandidateItemSet); // 閻庢稒锚閸嬪秹宕愬▎鎿勬嫹濡炪倕缍婂▔锟�
		return twoCandidateItemSet;
	}

	/**
	 * 计算项集中所有项的支持计数，K>=2
	 * 
	 * @param cItemset
	 * @return
	 */
	public static List<List<String>> getSupportedItemset( // 濞寸姴鍐柕鍡嫹2鐎殿噯鎷烽～鎰媼閿涘嫮鏆ù婊冾儎濞嗐垽寮敓锟�
			List<List<String>> cItemset) {
		getKItemTime = System.currentTimeMillis();
		boolean end = true;
		int totalCount = 0;
		int count = 0;
		List<List<String>> supportedItemset = new ArrayList<List<String>>();
		List<Integer> tempList = new ArrayList<Integer>();
		List<Integer> anotherTempList = new ArrayList<Integer>();
		for (int i = 0; i < cItemset.size(); i++) { // 閻庨潧缍婇。鍓佹崲娓氾拷锟介梻鍡楁閼垫垿鎯冮崟顒傛Ж濞戞搫鎷烽妴宥夊箥椤愶絽浼庨柡浣虹節闁叉粍绂嶇�顏勵潳閹艰揪鎷�
			// System.out.println();
			totalCount = 0;
			count = 0;
			for (int j = 0; j < transactions.size(); j++) {
				count = countSupportCount(transactions.get(j), cItemset.get(i)); // 缂備胶鍠曢鍝ユ媼閺夎法绉块柡渚婃嫹
				totalCount = totalCount + count;
				// System.out.print(count+"\r\n");
			}
			anotherTempList.add(totalCount);
			if (totalCount > MIN_SUPPORT) { // count闁稿﹦鍘ч妵鍥ㄧ鎼淬値娼愰悗瑙勫哺濡插洭宕愮涵椋庣闁告娅曞褏鎼剧�鐙�矗婵櫢鎷�
				tempList.add(totalCount); // 閻庢稒锚閸嬪秵锛愰幋鐘电暢濡炪倕缍婂▔锔界▔椤撱埄鏆ョ紒璁崇窔閵嗗秹鎯冮崟顏嗙殤濞寸姾鍩栭弳锟�
				supportedItemset.add(cItemset.get(i)); // supportedItemset閻庢稒蓱閺備焦锛愰幋鐘电暢濡炪倕缍婂▔锟�
				end = false;
			}

		}

		supporCount.add(tempList); // 閻庢稒锚閸嬪秴袙韫囧酣鍤嬪Λ鐗堝灩缁犳帗銇勮ぐ鎺撹偁闁汇劌瀚花銊︾閼稿灚娈�
		endTag = end; // 闁瑰憡鐗楃敮銏㈢磼閸噥鍓惧☉鎾抽閹拷

		return supportedItemset;
	}

	/**
	 * 计算单个项的支持计数
	 * 
	 * @param subsequence
	 * @param item
	 * @return
	 */
	private static int countSupportCount(List<String> subsequence,
			List<String> item) { // 閻犱緤绱曢悾缁樼鐎ｂ晜顐介柡渚婃嫹

		int eventCount = 0;
		int supportCount = 0;
		Boolean flag = false;
		List<Integer> eventIndex = new ArrayList<Integer>();
		List<String> precursorEvent = new ArrayList<String>();
		String event = new String();
		event = item.get(item.size() - 1); // 闁兼儳鍢茶ぐ鍥触鎼达絾鍩涘ù婊冾儎濞嗭拷
		String ddd = "";
		for (int k = 0; k < subsequence.size(); k++) {
			ddd = subsequence.get(k);
			if (event != null && event.equals(ddd)) { // 闁兼儳鍢茶ぐ鍥触鎼达絾鍩涘ù婊冾儎濞嗐垽宕烽妸銈囩殤濞寸姾娉涢悺娆愭償韫囨挸鐏欏☉鎿冨幘濞堟垹妲愰姀鐘电┛闁告瑱鎷�
				eventIndex.add(k);
			}
		}
		if (eventIndex.size() == 0) { // 閻庢稒鍔曠花顓㈠礆濡炴崘鍘☉鎾崇Т鐎垫﹢宕ラ銏″�缂備綀鍌滅殤濞寸媴鎷�
			supportCount = 0;
		} else {
			for (int i = 0; i < item.size() - 1; i++) { // 闁兼儳鍢茶ぐ鍥礈瀹ュ鏀冲ù婊冾儎濞嗐垺绋夐鐘崇暠闁告艾瀚柌婊咃拷閹邦亞鐨戝ù鐙呮嫹
				precursorEvent.add(item.get(i));
			}

			// 濞戞挶鍊撻柌婊勭鐎ｂ晜顐藉☉鏂款儔濡潡鎯冮崟顐ゆ瘓闁告牗妞藉Λ鍧楀及椤栨碍鍎婇悗娑櫭﹢顏堝礈瀹ュ鏀冲ù婊冾儎濞嗐垽鏁嶅畝鍐仧閻庢稒锚濠�亪宕氬▎搴ｇ殤濞寸姾鍩栭弳鐔煎礉閿燂拷
			flag = ifExistPrecursorEvent(subsequence, precursorEvent, -1,
					eventIndex.get(0));
			if (flag == true) {
				eventCount = 1;
			} else {
				eventCount = 0;
			}
			if (eventIndex.size() > 1) {
				for (int j = 0; j < eventIndex.size() - 1; j++) {
					flag = ifExistPrecursorEvent(subsequence, precursorEvent,
							eventIndex.get(j), eventIndex.get(j + 1));
					if (flag == true) {
						eventCount++;
					}
				}
			}
			supportCount = eventCount;
		}

		return supportCount;
	}

	/**
	 * 针对项集中的某一项，事务序列子区间内是否存在前驱事件
	 * 
	 * @param subsequence
	 * @param precursorEvent
	 * @param precursorIndex
	 * @param posteriorIndex
	 * @return flag
	 */
	public static Boolean ifExistPrecursorEvent(List<String> subsequence,
			List<String> precursorEvent, int precursorIndex, int posteriorIndex) {

		Boolean flag = false;
		Boolean havaThisItem = false;
		if (precursorIndex == posteriorIndex
				|| precursorIndex == (posteriorIndex - 1)
				|| (posteriorIndex - precursorIndex) - 1 < precursorEvent
						.size()) { // 闁兼眹鍎扮悮杈ㄧ▔椤忓棙绁查柛姘缁ㄣ劍绂掗崜浣圭ゲ闂侇厺绱槐婵嬪礆濞嗗繐闅橀梻鍌滅節缁楀锟藉Ο鐑樿含
			flag = false;
			return flag;
		} else {
			int i = posteriorIndex;
			int j = precursorEvent.size() - 1; // 閻犱焦婢樼欢閬嶅礄閿熶粙鏁嶇仦鑺ョ濞戞挸鎼幃妤呮閵忋垺鏆忛柣銊ュ濡插摜妲愰姀鐘电┛
			int k = 0;
			// 闁兼眹鍎辩亸顖炴⒒閺夋垵鐦堕柛姘煎亜婢х姵銇欓崣妯肩殤濞寸姾娉涢崹顖炲礈瀹ュ懎绠甸柛鎾崇Ч閳瑰秵绂嶇�鈺傤偨閹兼潙绻愰崹顏呯▔椤撶偛鐦堕柛姘煎亜椤﹁法浜搁幋婊堝殝濞存粌顑勫▎銏ゆ晬鐎靛儶ile鐎甸偊浜為獮鍡欎焊鏉堫偆绠婚悶娑樿嫰椤﹁法浜搁幋鐑嗗仹闁挎稑鐭佺�銏＄▔瀹ュ懎鐦堕柛姘煎亾缁辨繈宕氬▎搴ｇ煠闁告艾楠哥欢姘跺礈瀹ュ棙娈堕柨娑樻湰婢规﹢宕氶幍顕呭剳濞戞搫鎷烽柌婊勭▔瀹ュ懎鐦堕柛姘煎亞濞堟垹浜告潏鈺冩尝闁哄鎷�
			while (j >= 0) { // j閻炴稏鍔庨妵姘跺礈瀹ュ鏀冲ù婊冾儎濞嗐垺绋夐鐔烘Ж濞戞搩浜欑花銊︾閸撲焦鐣辩紒渚垮灩缁扁晠宕ｉ崙銈囩濞寸姴楠搁幃妤呭触閹存繂顤呭〒姘箖椤愬ジ骞栧鍛亶闁告挸绉归埞宥嗙鐎ｂ晜顐介柣銊ュ閻︹剝绋夐鍡楀亶鐎殿喗娲戠花銊︾閿燂拷
				havaThisItem = false;
				for (k = i - 1; k > precursorIndex; k--) { // 闁革负鍔岄悺娆愭償韫囨挸鐏欓柛鏍ㄦそ濡寧绋夐鐔奉棟闁告帗婢樻晶鐘炽仚閸欐鐨戝ù鐘虫构閼垫垿鎯冮崟顒傚帣濞戞搩浜欑花銊︾閿燂拷
					if (precursorEvent.get(j).equals(subsequence.get(k))) {// 閻庢稒鍔曠花顓㈠礆濡炴崘鍘柟鍨劤閸╁矂宕滃澶嗘敵濞存粌顑勫▎銏＄▔椤撶姵鐣遍柡灞惧姃闁叉粍绂嶇�鈺傤偨
						i = k;
						havaThisItem = true;
						break;
					}
				}
				if (k == (precursorIndex) && havaThisItem == false) { // 閻庢稒鍔曠花顓㈠礆濡わ拷闅橀梻鍌氱摠婢瑰倿骞撹箛鎾舵殮闁挎稑濂旂徊楣冨捶閵娿儳鎽嶉幖鏉戠箰閸亝绋夐鐔稿紦闁瑰灚鍎抽崺宀勫礈瀹ュ鏀冲ù婊冾儎濞嗐垺绋夐鐘崇暠闁哄本鍔掗柌婊勭鐎ｂ晜顐�
					flag = false;
					break;
				}

				if (j == 0 && havaThisItem == true) { // 闁告牕鎳庨幆鍫ュ礈瀹ュ鏀冲ù婊冾儎濞嗐垺绋夐鐘崇暠缂佹鍏涚粩瀛樸亜閿燂拷
					flag = true;
				}
				j--;
			}
		}
		return flag;
	}

	/**
	 * 获取二项集中所有项的置信度
	 * 
	 * @param fkItemSet
	 * @param fItemSet
	 */
	public static void getSecondConfidencedItemset(
			List<List<String>> fkItemSet, List<String> fItemSet) {
		List<List<String>> tempList = new ArrayList<List<String>>();
		double confidence = 0.0;
		List<String> anotherTempList = new ArrayList<String>();
		List<String> otherTempList = new ArrayList<String>();
		for (int i = 0; i < fkItemSet.size(); i++) {
			confidence = getSencondConfItem(fkItemSet.get(i), fkItemSet,
					fItemSet);
			anotherTempList.add(exportConfidence);
			if (confidence > MIN_CONF) {
				tempList.add(fkItemSet.get(i)); // lkItem.get(i)閻炴稏鍔庨妵姘▔閿熶粙鍤嬪Λ鐗堝灩缁犳帗銇勯惂鍝ョ濞戞挾鐦眎st<String>缂侇偉顕ч悗锟�
				otherTempList.add(exportConfidence);
			}
		}
		satisfiedConfidence.add(otherTempList); // 閻庢稒锚閸嬪秵绂嶅畝鍕╋拷濞存粌顑勫▎銏㈡喆閸曨偄鐏熼柣銊ュ閻ゅ棙绌遍垾鍐差唺闁哄嫬澧介妵锟�
		eventRules.add(tempList);

	}

	/**
	 * 获取二项集中某一项的置信度
	 * 
	 * @param fkItem
	 * @param fkItemSet
	 * @param fItemSet
	 * @return
	 */
	public static double getSencondConfItem(List<String> fkItem,
			List<List<String>> fkItemSet, List<String> fItemSet) {
		int currentItemNum = fkItemSet.get(0).size(); // 鐟滅増鎸告晶鐘炽亜瑜版帗鑲犲銈堫潐閺嗭拷
		int precursorItemNum = 1; // 闁告挸绉瑰鐗堛亜瑜版帗鑲犲銈堫潐閺嗭拷
		int oneItemPosition = 0; // 閻犱焦婢樼紞宥嗙▔閿熷锟藉Λ鐗堝灩缁犳帗銇勮ぐ鎺撹偁闁汇劌瀚鎼佸极閿燂拷
		double confidence = 0.0;
		String precursorEvent = new String();
		precursorEvent = fkItem.get(currentItemNum - 2); // 闁兼儳鍢茶ぐ鍥礈瀹ュ鏀冲ù婊冾儎濞嗭拷

		for (int i = 0; i < fItemSet.size(); i++) { // 闁兼儳鍢茶ぐ鍥礈瀹ュ鏀冲ù婊冾儎濞嗐垽宕烽妸銉ュ緭濡増鍨圭粻鎺撱亜瑜版帗鑲犲☉鎿冨幘濞堟垹妲愰姀鐘电┛
			if (fItemSet.get(i).equals(precursorEvent)) {
				oneItemPosition = i;
				break;
			}
		}

		int curerenEventPosition = findPosition(fkItem, fkItemSet); // 闁瑰灚鍎崇紞瀣礈瀹ュ嫮鐨戝ù鐙呮嫹闁告鍘栫花鈺傘亜瑜版帗鑲犲☉鎿冨幖閹洦銇勯敓浠嬪捶閵娿儱寰撳Λ鐗堝灩缁犳帗銇勮ぐ鎺撹偁濞戞搩鍘惧▓鎴犳閵忕姷绌�

		confidence = (double) (supporCount.get(currentItemNum - 1)
				.get(curerenEventPosition))
				/ supporCount.get(precursorItemNum - 1).get(oneItemPosition);
		exportConfidence = Integer.toString(supporCount.get(currentItemNum - 1)
				.get(curerenEventPosition))
				+ "/"
				+ Integer.toString(supporCount.get(precursorItemNum - 1).get(
						oneItemPosition));

		return confidence;
	}

	/**
	 * 获取k项集中所有项的置信度（K>2）
	 * 
	 * @param fkItemSet
	 * @param fItemSet
	 */
	public static void getConfidencedItemset(List<List<String>> fkItemSet,
			List<List<String>> fItemSet) {

		// System.out.print(fkItemSet.size());
		double confidence = 0.0;
		List<List<String>> tempList = new ArrayList<List<String>>();
		List<String> anotherTempList = new ArrayList<String>();
		List<String> otherTempList = new ArrayList<String>();
		for (int i = 0; i < fkItemSet.size(); i++) {
			confidence = getConfItem(fkItemSet.get(i), fkItemSet, fItemSet);
			anotherTempList.add(exportConfidence);
			if (confidence > MIN_CONF) {
				tempList.add(fkItemSet.get(i));

				otherTempList.add(exportConfidence);
			}
		}

		satisfiedConfidence.add(otherTempList); // 閻庢稒锚閸嬪秹宕ラ崟顏堝殝濞存粌顑勫▎銏㈡喆閸曨偄鐏熼柣銊ュ閻ゅ棙绌遍垾鍐差唺闁哄嫬澧介妵锟�
		eventRules.add(tempList);

	}

	/**
	 * 获取K项集中单个项的置信度，K>2
	 * 
	 * @param fkItem
	 * @param fkItemSet
	 * @param fItemSet
	 * @return
	 */
	public static double getConfItem(List<String> fkItem,
			List<List<String>> fkItemSet, List<List<String>> fItemSet) {

		int currentItemNum = fkItemSet.get(0).size(); // 鐟滅増鎸告晶鐘炽亜瑜版帗鑲犲銈堫潐閺嗭拷
		int precursorItemNum = fItemSet.get(0).size(); // 闁告挸绉瑰鐗堛亜瑜版帗鑲犲銈堫潐閺嗭拷

		double confidence = 0.0;
		List<String> precursorEvent = new ArrayList<String>();
		for (int i = 0; i < currentItemNum - 1; i++) { // 闁兼儳鍢茶ぐ鍥礈瀹ュ鏀冲ù婊冾儎濞嗭拷
			precursorEvent.add(fkItem.get(i));
		}

		int curerenEventPosition = findPosition(fkItem, fkItemSet); // 闁瑰灚鍎崇紞瀣礈瀹ュ嫮鐨戝ù鐘烘硾濠�亪宕楅懜闈涱暡闁革负鍔戦。鍓佹崲娓氾拷锟介梻鍡楁閼垫垿鎯冮崟顓炲亶鐎殿噯鎷�
		int precursorEventPosition = findPosition(precursorEvent, fItemSet); // 闁瑰灚鍎虫晶鐘炽仚閸欐鐨戝ù鐘烘硾濠�亪宕楅懜闈涱暡闁革负鍔戦。鍓佹崲娓氾拷锟介梻鍡楁閼垫垿鎯冮崟顓炲亶鐎殿噯鎷�

		confidence = ((double) supporCount.get(currentItemNum - 1).get(
				curerenEventPosition))
				/ supporCount.get(precursorItemNum - 1).get(
						precursorEventPosition);
		exportConfidence = Integer.toString(supporCount.get(currentItemNum - 1)
				.get(curerenEventPosition))
				+ "/"
				+ Integer.toString(supporCount.get(precursorItemNum - 1).get(
						precursorEventPosition));
		return confidence;
	}

	/**
	 * 找到K相集中某一项的位置，看》2
	 * 
	 * @param event
	 * @param fItemSet
	 * @return
	 */
	public static int findPosition(List<String> event, // k>=2
			List<List<String>> fItemSet) {
		int position = 0;
		for (int i = 0; i < fItemSet.size(); i++) {
			if (judgeEqual(event, fItemSet.get(i))) { // 闁告帇鍊栭弻鍥礈瀹ュ鏀冲銈堫潐濡叉悂宕ラ敃锟藉fItemSet濞戞搩鍘惧▓鎴炪亜鐟欏嫭笑闁告熬濡囧ù澶愬触閿燂拷
				position = i;
				break;
			}
		}

		return position;
	}

	public static Boolean judgeEqual(List<String> str, List<String> anotherStr) { // 濞撴碍绻冮鐓幮掗弮鍥╃獩list濞戞搩鍘惧▓鎴澬掕箛搴ㄥ殝濡炪倧鎷�
		Boolean flag = true;
		for (int i = 0; i < str.size(); i++) {
			if ((str.get(i).equals(anotherStr.get(i))) == false) {
				flag = false;
				break;
			}
		}
		return flag;
	}

	/**
	 * 产生下一个候选项集
	 * 
	 * @param lItemset
	 * @return nextcItemset
	 */
	public static List<List<String>> getNextCandidate( // 闁汇垻娲�1濡増鍨圭粻鎺撱亜瑜版帗鑲犻柤鎯у槻瑜板槚濡炪倗鎳撻敓浠嬫焻婢舵劖鑲犻柨娑樼焸閿熻姤娼婚崶顏勬閺夆晝鍋炵敮鎾箼瀹ュ嫮绋�
														// K>=3
			List<List<String>> lItemset) {

		List<List<String>> nextcItemset = new ArrayList<List<String>>();
		Boolean flag = true;
		int ItemsetNum = lItemset.get(0).size(); // 濡炪倛顫夐弳鐑�
		for (int i = 0; i < lItemset.size(); i++) { // 闁汇垻鍠愰崹姘▔閿熻棄纾瑰ù婊冪焸閵嗗秹宕愬▎鎿勬嫹濡炪倕缍婂▔锟�
			for (int j = 0; j < lItemset.size(); j++) { // 闁告帇鍊栭弻鍥ㄦ交閻愭潙澶嶉柛鎾崇Ч閵嗗秹鎯冮崟顐ｅ�k-1濡炪倛顫夊Σ鎼佸触閿曪拷瀚查弶鈺冨仦鐢挳宕ユ惔銊ｏ拷闁汇劌瀚晶鐖�1濡炪倛顫夊Σ鎼佸触閿旂偓绁查柛姘炬嫹
				if (i != j) {
					flag = true;
					for (int m = 0; m < ItemsetNum - 1; m++) { // 闁告帇鍊栭弻鍥ㄦ交閻愭潙澶嶉柛鎾崇Ч閵嗗秹鎯冮崟顐ｅ�k-1濡炪倛顫夊Σ鎼佸触閿曪拷瀚查弶鈺冨仦鐢挳宕ユ惔銊ｏ拷闁汇劌瀚晶鐖�1濡炪倛顫夊Σ鎼佸触閿旂偓绁查柛姘炬嫹
						if ((lItemset.get(i).get(m + 1).equals(lItemset.get(j)
								.get(m))) == false) {
							flag = false;
							break;
						}
					}
					if (true == flag
							&& (lItemset.get(i).get(0).equals(lItemset.get(j)
									.get(ItemsetNum - 1))) == false) {
						List<String> tempList = new ArrayList<String>();
						for (int k = 0; k < ItemsetNum; k++) {
							tempList.add(lItemset.get(i).get(k));
						}
						tempList.add(lItemset.get(j).get(ItemsetNum - 1));
						nextcItemset.add(tempList); // 鐎电増顨呴崺宀勫棘閺夊尅鎷烽梺顐㈩樀閵嗗秹姊块敓锟�
					}
				}

			}
		}
		return nextcItemset;
	}

}
