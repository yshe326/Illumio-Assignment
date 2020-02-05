# Illumio-Assignment

1, I use HashMap data structure to store the input data: "Map<String, Map<Integer, List<long[]>>>". The key is the combination of direction and protocol. The value is also be a map, in that map, the key is the port number, and the value is a Long array list which will contain all the range of IP address, like [IP1, IP2], in ascending order according to the first value.

2, I thansform the String IP address into a long value, which is easy to search and compare.

3, I use binary-search to insert the IP range into the arraylist and use the same function to find if the list of certain port number contain the certain IP adress

4, Assume the input rule number is N, when loading the data, time complexity is O(N*log(N)), the worst case space complexity is (65535 * N); when calling the acceptPacket function, the time complexity is O(log(N)).

5, Team priority: platform team > policy team > data team

6, I'm available on Monday, Wednesday, and Friday, 9am - 5pm for possible interview.

7, I will graduate on Jan, 2021. I could do the intership more than three months.
