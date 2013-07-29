guard :shell do
  watch(/(.+)\.java/) do |m|
    title = "Test output"
    msg = "Java error"
    status = :failed

    output = `mvn test -q`

    if $?.success?
      msg = output
      status = :success
    end

    n msg, title, status

    "-> #{msg}"
  end
end

# Add files and commands to this file, like the example:
#   watch(%r{file/path}) { `command(s)` }
#
guard 'shell' do
  watch(/(.*).txt/) {|m| `tail #{m[0]}` }
end
