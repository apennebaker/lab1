guard :shell do
  watch(/(.+)\.java/) do |m|
    title = "Test output"
    status = :failed

    msg = `mvn test -q`

    if $?.success?
      status = :success
    end

    n msg, title, status

    "-> #{msg}"
  end
end
