# CPD Project 1

## Problem description and algorithms explanation
### Problem 1
In the first problem, we were instructed to apply the conventional matrix multiplication procedure, which involves multiplying each column of the second matrix by one line of the first matrix. In addition to the algorithm written in C++ that was published, Lua was the language we chose for that implementation.<br>

### Problem 2
For this problem, the procedure was unusual in that each iteration involved "blocking" entries from the first matrix by multiplying them by the matching line in the second matrix. This method was more effective than the first since, in theory, there is a greater likelihood that a line will be saved in cache than a column when accessing matrix members. In this second scenario, the values are retrieved quicker (with less cache misses).
<br>

### Problem 3
The original matrices in this last procedure are divided into blocks of smaller matrices to make the calculation easier and increase efficiency.<br><br>

## Performance metrics

After analysing the metrics provided by PAPI, we decided to use the ones pre-coded (PAPI_L1_DCM and PAPI_L2_DCM). The purpose of DCM metrics is to evaluate the memory efficiency of each algorithm by counting the total number of data cache misses. This value alone is not relevant, but when comparing results for different algorithms, it is possible to arrive to appropriate conclusions.
<br>

## Results and analysis
We chose to compare implementations to better demonstrate efficiency.
<br><br>

### Implementation in Lua

The efficiency of the same methods was generally substantially slower in Lua than it was in C++. One of the causes of this is because Lua is an interpreted language, which means that it requires a higher degree of abstraction before it can be translated into machine code that can be executed.
<br><br>

<img src="/assign1/images/chart1.png" width="500"/>

*Graphic comparing the execution times of the multiplication of matrices of different sizes in Lua and C++*
<br><br>

<img src="/assign1/images/chart2.png" width="500"/>

*Graphic comparing the execution times of the multiplication of matrices through the lines method of different sizes in Lua and C++*
<br><br>

In general, Lua may not be as efficient as C/C++ when it comes to matrix multiplication due to differences in how memory is managed. Lua uses a private heap to manage all of its objects and data structures, which is handled by the Lua memory manager. The memory manager has various components that manage dynamic storage aspects such as sharing, segmentation, pre-allocation, and caching. However, it's worth noting that the specifics of how memory management works can vary between different programming languages and can impact performance differently. In our specific case, we observed that the first method for matrix multiplication was more efficient than the second method, even though the second method was accessing values in the same cache line.
<br><br>

### Comparison between first method and second method
<br>

We conducted a comparison of the first and second methods by analyzing C++ implementations and observing that all metrics increased, albeit at different rates. The first method exhibited consistently higher growth rates than the second.<br><br>

<img src="/assign1/images/chart5.png" width="500"/>

*Graphic comparing the execution times of the multiplication of matrices through the first and second method of different sizes in C++*
<br><br>


When analyzing the cache misses charts for L1 and L2, we found that the first algorithm had higher values and growth due to its implementation. The memory manager prioritizes storing data that is located close together in the code, which means that a line of a matrix has a higher chance of being stored in the cache than the nth element of every line of that matrix. As a result, the second algorithm requires less time to compute the multiplication result and has a lower likelihood of missing the data required for such a calculation.

<img src="/assign1/images/chart6.png" width="500"/>

*Graphic comparing the data cache misses of L1 when multiplicating matrices through the first and second method of different sizes in C++*
<br><br>

<img src="/assign1/images/chart7.png" width="500"/>

*Graphic comparing the data cache misses of L2 when multiplicating matrices through the first and second method of different sizes in C++*
<br><br>

### Comparison between the second and third methods
<br>

In general, the block method is more efficient due to the way it divides a large matrix into smaller blocks. This allows all the necessary values for each block's multiplication to be stored in cache, which reduces the chances of cache misses and, consequently, reduces the execution time.
<br>

When comparing the execution time of both the block and traditional methods, the former is more efficient, as evidenced by a difference of 140.796 seconds in the multiplication of a 10240x12040 matrix.
<br>

The results of our tests show that smaller block sizes lead to faster execution times for the matrix multiplication algorithm. For example, using a block size of 256 for a matrix with 4096 rows and columns resulted in a time of 32.690 seconds, while using a block size of 512 for the same matrix increased the time to 39.316 seconds. This is likely due to the fact that smaller block sizes result in less data being stored in cache, reducing the likelihood of cache misses and improving performance.

We also observed that the relationship between block size and execution time varies depending on the size of the matrices being multiplied. For example, using a block size of 512 for a matrix with 10240 rows and columns resulted in a time of 507.672 seconds, while using a block size of 256 for the same matrix resulted in a time of 518.045 seconds. However, for a matrix with 6144 rows and columns, using a block size of 512 resulted in a faster time than using a block size of 256. This suggests that the optimal block size for matrix multiplication depends on the specific dimensions of the matrices being multiplied.
<br>

<img src="/assign1/images/chart3.png" width="500"/>

*Graphic displaying the execution times when multiplicating bigger matrices through the second method of different sizes in C++*
<br><br>

<img src="/assign1/images/chart4.png" width="500"/>

*Graphic comparing the execution times when multiplicating matrices through the third method of different sizes and different block sizes in C++*
<br><br>

We anticipated that the third method would result in lower cache misses for both the first and second caches. Our results confirmed this expectation, with the difference between the methods increasing as matrix size increased. The largest difference was observed when comparing the cache misses of the first cache during the multiplication of 10240x10240 matrices, with a difference approximately 1366390000000
<br>
This expected difference can be attributed to the fact that for larger matrices, entire lines cannot fit into cache lines. By dividing the matrix into smaller blocks, each block can fit entirely into cache, reducing the number of cache misses and leading to improved performance.
<br><br>

<img src="/assign1/images/chart8.png" width="500"/>

*Graphic displaying the number of cache 1 misses when multiplicating matrices through the second method of different sizes in C++*
<br><br>

<img src="/assign1/images/chart9.png" width="500"/>

*Graphic displaying the number of cache 2 times when multiplicating matrices through the second method of different sizes in C++*
<br><br>

<img src="/assign1/images/chart10.png" width="500"/>

*Graphic comparing the number of cache 1 misses when multiplicating matrices through the third method of different sizes and different block sizes in C++*
<br><br>

<img src="/assign1/images/chart11.png" width="500"/>

*Graphic comparing the number of cache 1 misses when multiplicating matrices through the third method of different sizes and different block sizes in C++*
<br><br>


## Conclusions

After examining the time spent on each implementation, we concluded that the matrix multiplication algorithm was most efficient in C/C++ and least efficient in Lua. Additionally, we found that C/C++ was substantially faster than Lua in every single test conducted. <br>

We also observed that the number of cache misses was a key factor in the higher execution time of the C/C++ implementation. A significantly lower number of cache misses resulted in a significantly lower execution time, as seen in the charts. Therefore, it is important for programmers to be mindful of cache optimization when working with low-level languages like C/C++. <br>

In contrast, we noticed that what is faster in a low-level language may not necessarily be faster in a higher-level language. While Lua offers several levels of abstraction to simplify memory management and reduce errors, it leads to more unpredictable memory usage and can make it challenging for programmers to ensure efficient memory management. <br>

Overall, this project helped us gain a better understanding of how the cache stores data and the impact of memory management on code efficiency, particularly in the context of matrix multiplication programs. <br>

<br><br>

## Group Members - T10G17
- Eduardo Silva (up202004999)
- Hugo Castro (up202006770)
- Luís Paiva (up202006094)
<br><br>

## Appendix

### Results of both implementations of the first method from sizes 600 to 3000

| Size Line/Cols | Time C++ | Time Lua | L1 DCM      | L2 DCM      |
| -------------- | -------- | -------- | ----------- | ----------- |
| 600            | 0,197    | 5,298    | 244781084   | 42949576    |
| 1000           | 1,323    | 26,44    | 1230036345  | 289955690   |
| 1400           | 3,756    | 82,803   | 3488325371  | 1336208959  |
| 1800           | 18,612   | 179,864  | 9060959733  | 7639113435  |
| 2200           | 40,091   | 331,617  | 17661266408 | 24211998524 |
| 2600           | 71,595   | 539,978  | 30873133513 | 50357817104 |
| 3000           | 118,191  | 846,849  | 50293529274 | 96689216833 |

### Results of both implementations of the second method from sizes 600 to 3000

| Size Line/Cols | Time C++ | Time Lua | L1 DCM     | L2 DCM     |
| -------------- | -------- | -------- | ---------- | ---------- |
| 600            | 0,097    | 10,683   | 27103016   | 56602913   |
| 1000           | 0,477    | 48,908   | 125800935  | 259187973  |
| 1400           | 1,624    | 138,088  | 346296896  | 686026585  |
| 1800           | 3,916    | 288,039  | 745324244  | 1363778720 |
| 2200           | 6,556    | 523,174  | 2070711313 | 2496612430 |
| 2600           | 10,746   | 855,936  | 4411761211 | 4128310356 |
| 3000           | 16,444   | 1340,063 | 6779068183 | 6318215605 |

### Results of C++ implementation of the second method from sizes 4096 to 10240
| Size Line/Cols | Time C++ | L1 DCM      | L2 DCM      |
| -------------- | -------- | ----------- | ----------- |
| 4096           | 42,165   | 17546502405 | 15713320668 |
| 6144           | 142,569  | 59246598314 | 54660177257 |
| 8192           | 339,941  | 1,40167E+11 | 1,25699E+11 |
| 10240          | 648,468  | 2,73694E+11 | 2,59413E+11 |

### Results of C++ implementation of the third method from sizes 4096 to 10240 with block sizes 128, 256, 512
| Block Size | Size Line/Cols | Time C++ | L1 DCM      | L2 DCM      |
| ---------- | -------------- | -------- | ----------- | ----------- |
| 128        | 4096           | 35,529   | 9934518036  | 33908811434 |
| 256        | 4096           | 32,69    | 9157315398  | 23506865034 |
| 512        | 4096           | 39,316   | 8769318197  | 19328040445 |
| 128        | 6144           | 119,011  | 33530806812 | 1,15728E+11 |
| 256        | 6144           | 112,744  | 30896369292 | 78665810754 |
| 512        | 6144           | 109,882  | 29639249218 | 67709570057 |
| 128        | 8192           | 282,765  | 79500486862 | 2,68099E+11 |
| 256        | 8192           | 338,459  | 73306441945 | 1,74852E+11 |
| 512        | 8192           | 332,703  | 72032162256 | 1,43655E+11 |
| 128        | 10240          | 564,106  | 1,55267E+11 | 5,21737E+11 |
| 256        | 10240          | 518,045  | 1,43028E+11 | 3,60676E+11 |
| 512        | 10240          | 507,672  | 1,37054E+11 | 3,09199E+11 |
