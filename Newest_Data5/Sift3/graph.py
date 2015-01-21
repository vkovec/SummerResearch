import numpy as np
print np.__version__
import matplotlib.pyplot as plt

file = 'gc2q.txt'

#y = np.loadtxt('cs2.txt', delimiter = ',', skiprows = 999000,usecols = [0])
up = np.loadtxt(file, delimiter = ',',usecols = [0])
down = np.loadtxt(file, delimiter = ',',usecols = [1])
left = np.loadtxt(file, delimiter = ',',usecols = [2])
right = np.loadtxt(file, delimiter = ',',usecols = [3])
o1 = np.loadtxt(file, delimiter = ',',usecols = [9])
o2 = np.loadtxt(file, delimiter = ',',usecols = [8])
o3 = np.loadtxt(file, delimiter = ',',usecols = [7])
o4 = np.loadtxt(file, delimiter = ',',usecols = [6])
"""o1 = np.loadtxt(file, delimiter = ',',usecols = [15])
o2 = np.loadtxt(file, delimiter = ',',usecols = [18])
o3 = np.loadtxt(file, delimiter = ',',usecols = [16])
o4 = np.loadtxt(file, delimiter = ',',usecols = [14])"""
#print y

#generate some data
x = np.array(range(len(up)))
#x = np.arange(0, 75, 5)

#print x

#y = 3 + 0.5 * x + np.random.randn(20)

#plot the data
#plt.plot(x, up, marker = 'o', linestyle ='-', color = 'r', label = "Up")
fig = plt.figure()
plt.plot(x, up, linestyle ='-', color = 'r', label = "Up")
plt.plot(x, down, linestyle ='-', color = 'b', label = "Down")
plt.plot(x, left, linestyle ='-', color = 'g', label = "Left")
plt.plot(x, right, linestyle ='-', color = 'k', label = "Right")
plt.plot(x, o1,linestyle ='-', color = 'c', label = "o1")
plt.plot(x, o2, linestyle ='-', color = 'm', label = "o2")
plt.plot(x, o3, linestyle ='-', color = 'y', label = "o3")
plt.plot(x, o4, linestyle ='-', color = '0.75', label = "o4")
"""plt.plot(x, o1,linestyle ='-', color = 'c', label = "o9")
plt.plot(x, o2, linestyle ='-', color = 'm', label = "o10")
plt.plot(x, o3, linestyle ='-', color = 'y', label = "o11")
plt.plot(x, o4, linestyle ='-', color = '0.75', label = "o12")"""
plt.legend()
plt.title("Q values of Actions over Time (State 17)")
plt.show()
fig.savefig('qVal.png')


file = 'gc2pj.txt'

#'good' options
o1 = np.loadtxt(file, delimiter = ',', usecols = [5])
o3 = np.loadtxt(file, delimiter = ',',usecols = [3])
o5 = np.loadtxt(file, delimiter = ',',usecols = [1])
o7 = np.loadtxt(file, delimiter = ',',usecols = [15])
o9 = np.loadtxt(file, delimiter = ',',usecols = [11])
o11 = np.loadtxt(file, delimiter = ',',usecols = [12])
o13 = np.loadtxt(file, delimiter = ',',usecols = [9])
o15 = np.loadtxt(file, delimiter = ',',usecols = [7])

fig = plt.figure()
plt.xlim([0,1000])
plt.plot(x, o1, linestyle ='-', color = 'r', label = "o1")
plt.plot(x, o3, linestyle ='-', color = 'b', label = "o3")
plt.plot(x, o5, linestyle ='-', color = 'g', label = "o5")
plt.plot(x, o7, linestyle ='-', color = 'k', label = "o7")
plt.plot(x, o9,linestyle ='-', color = 'c', label = "o9")
plt.plot(x, o11, linestyle ='-', color = 'm', label = "o11")
plt.plot(x, o13, linestyle ='-', color = 'y', label = "o13")
plt.plot(x, o15, linestyle ='-', color = '0.75', label = "o15")
plt.legend()
plt.title("# of States Taking \"Good\" Options over Time")
plt.xlabel("Trial")
plt.ylabel("# of States taking Option")
plt.show()
#fig.savefig('good.png')
fig.savefig('good.svg', format='svg', dpi=1200)

#'bad' options
o2 = np.loadtxt(file, delimiter = ',',usecols = [4])
o4 = np.loadtxt(file, delimiter = ',',usecols = [2])
o6 = np.loadtxt(file, delimiter = ',',usecols = [0])
o8 = np.loadtxt(file, delimiter = ',',usecols = [13])
o10 = np.loadtxt(file, delimiter = ',',usecols = [14])
o12 = np.loadtxt(file, delimiter = ',',usecols = [10])
o14 = np.loadtxt(file, delimiter = ',',usecols = [8])
o16 = np.loadtxt(file, delimiter = ',',usecols = [6])

fig = plt.figure()
plt.xlim([0,1000])
plt.plot(x, o2, linestyle ='-', color = 'r', label = "o2")
plt.plot(x, o4, linestyle ='-', color = 'b', label = "o4")
plt.plot(x, o6, linestyle ='-', color = 'g', label = "o6")
plt.plot(x, o8, linestyle ='-', color = 'k', label = "o8")
plt.plot(x, o10,linestyle ='-', color = 'c', label = "o10")
plt.plot(x, o12, linestyle ='-', color = 'm', label = "o12")
plt.plot(x, o14, linestyle ='-', color = 'y', label = "o14")
plt.plot(x, o16, linestyle ='-', color = '0.75', label = "o16")
plt.legend()
plt.title("# of States Taking \"Bad\" Options over Time")
plt.xlabel("Trial")
plt.ylabel("# of States taking Option")
plt.show()
#fig.savefig('bad.png')
fig.savefig('bad.svg', format='svg', dpi=1200)
