# About
In a face-to-face job interview, I was asked to solve this problem:
- In a big file of IPs, how to find the top 10 most popular IPs? The file size can be several TB.

I was not able to figure out a solution at the moment. I felt very bad about that, did some research and implemented the solution.

# Solution

- An IP address (xxx.xxx.xxx.xxx) can be represented by a positive integer(long) value. the value range is (0, 0xffffffffL)

- We can define an array of long with the size 0xffffffffL, then use the element index to represent the IP, 
use the element value to represent the visit time of the corresponding IP.

- To define such an array, we need a memory space of 2^32 * 8 bytes, which is 32 GB.

- We can not make use of such big memory. But we can split things up to small scales and make use of this idea.

- For example, we can segment the original IP file to 256 small IP files:

	file0:		0.xxx.xxx.xxx
	file1:		1.xxx.xxx.xxx
	
	... 
	file255:		255.xxx.xxx.xxx

- Then we can find the most popular IPs in each segment, and then nail down the most popular IP in all 256 segments.

- To find the most popular IP in one segment by using the idea, we can define an array of long with size 0xffffffL, which requires 2^24 * 8 bytes (128 MB) memory usage. That is totally acceptable.

- Having the solution worked out, the rest is coding.

# Result
I tested the code with different size of data. Sadly I don't have disk space to store TB of data.

	Number Of IPs:			0xFFFF
	File Size:				913.9KB
	Total time elapsed: 	13 seconds
	
	Number Of IPs:			0xFFFFF
	File Size:				14.3MB
	Total time elapsed: 	13 seconds
	
	Number Of IPs:			0xFFFFFF
	File Size:				228.5MB
	Total time elapsed: 	25 seconds
	
	Number Of IPs:			0xFFFFFFF
	File Size:				3.6GB
	Total time elapsed: 	318 seconds
	
	Number Of IPs:			0xFFFFFFFF
	File Size:				57.1GB
	Total time elapsed: 	4993 seconds
	
I hope I would have chance to improve these data in the future.
