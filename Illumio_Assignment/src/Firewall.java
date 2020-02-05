import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Firewall {
    /**
     * InputMap stores input rules. The key is the combination of direction and protocol. The value will also be a map,
     * in that map, the key is the port number, and the value is a long array list which will contain all the range of
     * IP address, like[IP1, IP2], in ascending order according to the first value.
     * I use long value to represent the IP address, which is easy to be inserted and searched.
     */
    private Map<String, Map<Integer, List<long[]>>> inputMap;

    /**
     * Constructor
     * @param file the path of the input file, like ./input/fileName.csv
     */
    public Firewall(String file) {
        inputMap = new HashMap<>();
        // read input file
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                loadData(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Takes four arguments and returns a boolean: true, if there exists a rule in the file that this object was
     * initialized with that allows traffic with these particular properties, and false otherwise.
     * @param direction direction
     * @param protocol protocol
     * @param port port
     * @param IP IP address
     * @return true or false
     */
    public boolean acceptPacket(String direction, String protocol, int port, String IP) {
        String dirAndPro = direction + protocol;
        if (!inputMap.containsKey(dirAndPro)) {
            return false;
        }
        Map<Integer, List<long[]>> portIPMap = inputMap.get(dirAndPro);
        if (!portIPMap.containsKey(port)) {
            return false;
        }
        List<long[]> IPList = portIPMap.get(port);
        long IPNum = stringToLongIP(IP);
        int pos = binarySearch(IPNum, IPList);

        // if pos == 0, it means that every IP num in the list is bigger than the IPNum, return false.
        // IPList.get(pos - 1)[0] must be <= the IPNum, so, if IPList.get(pos - 1)[1] >= IPNum, return true.
        return pos != 0 && IPList.get(pos - 1)[1] >= IPNum;
    }

    /**
     * Load one rule into the inputMap, such as "inbound,tcp,80,192.168.1.2"
     * @param data rule
     */
    private void loadData(String data) {
        String[] inputs = data.split(",");
        String dirAndPro = inputs[0] + inputs[1];
        inputMap.putIfAbsent(dirAndPro, new HashMap<>());

        String[] range;
        int[] portRange = new int[2];
        long[] IPRange = new long[2];

        //Port number range, [p1, p2], if it is a single number, then p1 == p2
        range = inputs[2].split("-");
        if (range.length == 1) {
            int portNum = Integer.parseInt(range[0]);
            portRange[0] = portNum;
            portRange[1] = portNum;
        } else {
            portRange[0] = Integer.parseInt(range[0]);
            portRange[1] = Integer.parseInt(range[1]);
        }

        //IP address range, [IP1, IP2], if it is a single address, then IP1 == IP2
        range = inputs[3].split("-");
        if (range.length == 1) {
            long IPNum = stringToLongIP(range[0]);
            IPRange[0] = IPNum;
            IPRange[1] = IPNum;
        } else {
            IPRange[0] = stringToLongIP(range[0]);
            IPRange[1] = stringToLongIP(range[1]);
        }

        Map<Integer, List<long[]>> portIPMap = inputMap.get(dirAndPro);
        for (int p = portRange[0]; p <= portRange[1]; p++) {
            portIPMap.putIfAbsent(p, new ArrayList<>());
            List<long[]> IPList = portIPMap.get(p);

            // Use binary search to find the correct position to insert the new IP range.
            int pos = binarySearch(IPRange[0], IPList);
            IPList.add(pos, IPRange);
        }

    }

    /**
     * @param IP IP address in String representation
     * @return IP address in long representation
     */
    private long stringToLongIP(String IP) {
        long IPNum = 0;
        String[] address = IP.split(".");
        for (String str : address) {
            IPNum = IPNum * 1000 + Long.parseLong(str);
        }
        return IPNum;
    }

    /**
     * Find the first IP range in the list that the range[0] is bigger than the target.
     * @param target the IP address we want to find
     * @param list IP range list
     * @return the position to insert the new IPRange
     */
    private int binarySearch(long target, List<long[]> list) {
        int l = 0;
        int r = list.size();
        while (l < r) {
            int mid = l + (r - l) / 2;
            long[] IPRange = list.get(mid);
            if (IPRange[0] <= target) {
                l = mid + 1;
            } else {
                r = mid;
            }
        }
        return l;
    }

    /**
     * @param args args[0] is the input file, args[1] is the test file
     */
    public static void main(String[] args) {
        Firewall firewall = new Firewall(args[0]);
        int count = 0;
        int passNum = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(args[1]));
            String line;
            while ((line = br.readLine()) != null) {
                String[] testCase = line.split(",");

                String dir = testCase[0];
                String pro = testCase[1];
                int port = Integer.parseInt(testCase[2]);
                String IP = testCase[3];

                boolean label =Boolean.parseBoolean(testCase[4]);
                boolean predict = firewall.acceptPacket(dir, pro, port, IP);
                if (label && predict || !label && !predict) {
                    passNum++;
                } else {
                    System.out.println("testCase: " + line + " is WRONG");
                }
                count++;
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Number of passed test cases: " + passNum + "/" + count);
    }
}
