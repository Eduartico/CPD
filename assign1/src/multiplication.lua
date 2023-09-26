function OnMult(row, col)
    local T1 = os.clock()
    local matrix_a, matrix_b, matrix_c = {}, {}, {}

    for i = 0, row - 1 do
        for j = 0, col - 1 do
            matrix_a[i*row + j] = 1
        end
    end

    for i = 0, row - 1 do
       for j = 0, col - 1 do
            matrix_b[i*col + j] = (i+1)
       end
    end

    for i = 0, col - 1 do
        for j = 0, col - 1 do
             matrix_c[i*col + j] = 0
        end
     end

     for i = 0, row - 1 do
        for j = 0, col - 1 do
            local sum = 0
            for k = 0, row - 1 do
                sum = sum + matrix_a[i*row + k] * matrix_b[k*col + j]
            end
            matrix_c[i*col + j] = sum
        end
    end
    

    local T2 = os.clock()
    local st = string.format("OnMult function time was: %3.3f seconds\n", (T2-T1))

    print(st)
    io.write("Result (First Line Only):\n")
    for i = 0, math.min(10, row-1) do
        io.write(matrix_c[i*col].." ")
    end
    io.write("\n")

end

function OnMultline(row, col)
    local T1 = os.clock()
    local matrix_a, matrix_b, matrix_c = {}, {}, {}

    for i = 0, row - 1 do
        for j = 0, col - 1 do
            matrix_a[i*row + j] = 1
        end
    end

    for i = 0, col - 1 do
       for j = 0, col - 1 do
            matrix_b[i*col + j] = (i+1)
       end
    end

    for i = 0, col - 1 do
        for j = 0, col - 1 do
             matrix_c[i*col + j] = 0
        end
     end



    for i = 0, row - 1 do
        for k = 0, col - 1 do
            for j = 0, row - 1 do
                matrix_c[i * row + j] = matrix_c[i * row + j] + matrix_a[i * row + k] * matrix_b[k * col + j]
            end
        end
    end

    local T2 = os.clock()
    local st = string.format("OnMultline function time was: %3.3f seconds\n", (T2-T1))
    print(st)
    io.write("Result (First Line Only):\n")
    for i = 0, math.min(10, row-1) do
        io.write(matrix_c[i*col].." ")
    end
    io.write("\n")

end


io.write("\n-----------------------10240-----------------------------------------\n")
OnMult(3000,3000)
OnMultline(3000,3000)

